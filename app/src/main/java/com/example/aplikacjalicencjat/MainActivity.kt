package com.example.aplikacjalicencjat

import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_point_menu.*
import kotlinx.android.synthetic.main.check_point_location_options.*
import kotlinx.android.synthetic.main.points_menu.*
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 10
    }

    // rozmiary wyświetlacza
    private val deviceWidth = Resources.getSystem().displayMetrics.widthPixels
    //private val deviceHeight = Resources.getSystem().displayMetrics.heightPixels


    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var pointsListDialong: Dialog
    private lateinit var addPointDialog: Dialog
    private lateinit var checkPointLocationDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // mapa
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.setPadding((deviceWidth/2) - 70, 0, 0, 0)

        // domyślny widok mapy
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(52.069167, 19.480556), 6f))

        // ukrycie domyślnego przycisku lokalizacji
        mMap.uiSettings.isMyLocationButtonEnabled = false


        // marker lista

        val testowePunkty: ArrayList<MyPoint> = arrayListOf(
            MyPoint(51.253187, 22.517707, "Początek", "startPoint"),
            MyPoint(51.253653, 22.534400, "", "midPoint"),
            MyPoint(51.253453, 22.533400, "", "midPoint"),
            MyPoint(51.253753, 22.534500, "", "midPoint"),
            MyPoint(51.253253, 22.516500, "Koniec", "endPoint"))

        //generateMarkers(testowePunkty)

        // markery ostateczna wersja z bazy danych!!!!!!!!!!!!!!!
        // baza danych
        val dbHelper = DataBaseHelper(applicationContext)
        val db = dbHelper.writableDatabase

        // dodawanie markerów
        // marker 1 - początek
        val value = ContentValues()
        value.put("latitude", 51.253187)
        value.put("longitude", 22.517707)
        value.put("name", "Marker 1 - nm")
        value.put("type", "startPoint")

        db.insertOrThrow(TableInfo.TABLE_NAME, null, value)
        value.clear()

        // marker 2 - środek
        value.put("latitude", 51.253653)
        value.put("longitude", 22.534400)
        value.put("name", "Marker 2 - nm")
        value.put("type", "midPoint")

        db.insertOrThrow(TableInfo.TABLE_NAME, null, value)
        value.clear()

        // marker 3 - środek
        value.put("latitude", 51.253453)
        value.put("longitude",22.533400)
        value.put("name", "Marker 3 - nm")
        value.put("type", "midPoint")

        db.insertOrThrow(TableInfo.TABLE_NAME, null, value)
        value.clear()

        // marker 4 - koniec
        value.put("latitude",51.253753)
        value.put("longitude", 22.534500)
        value.put("name", "Marker 4 - nm")
        value.put("type", "endPoint")

        db.insertOrThrow(TableInfo.TABLE_NAME, null, value)
        value.clear()


        // obsługa własnego przycisku lokalizacji + ew. prośba o pozwolenie pozyskania lokacji
        geolocateButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    locateMe()
                } else {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE)
                }
            }
        }

        // otwieranie dialogu z listą punktów
        pointsListDialong = Dialog(this)
        addPointDialog = Dialog(this)
        checkPointLocationDialog = Dialog(this)

        pointsButton.setOnClickListener {
            openPointsListDialog(db)
        }

        // otwieranie dialogu z dodawaniem punktu
        pointsListDialong.setOnShowListener {
            // obsługa przycisku do otwierania dialogu z dodawaniem punktu
            pointsListDialong.addPointButton.setOnClickListener {
                openAddPointDialog()
            }
        }

        // obsługa przycisków dodawania punktu i sprawdzania
        addPointDialog.setOnShowListener {
            // obsługa przycisku do sprawdzania lokalizacji
            addPointDialog.checkLocationBT.setOnClickListener {
                // TODO wywołanie funkcji
                // tymczasowe wyłączenie przycisków na ekranie głównym
                pointsButton.visibility = View.INVISIBLE
                settingsButton.visibility = View.INVISIBLE
                geolocateButton.visibility = View.INVISIBLE

                checkLocation()
            }

            // obsługa przycisku do dodawania punktu do bazy danych
            addPointDialog.addPointToDatabaseBT.setOnClickListener {
                // TODO wywołanie funkcji
            }
        }

        // obsługa przyciskow dialogu ze sprawdzaniem lokalizacji wpisanego punktu
        checkPointLocationDialog.setOnShowListener {
            checkPointLocationDialog.check_point_edit_BT.setOnClickListener {
                // TODO WRACA DO OKNA Z ADD POINT I UMOŻLIWIA EDYCJĘ WSZYSTKIEGO
                checkPointLocationDialog.dismiss()
                addPointDialog.show()
            }

            checkPointLocationDialog.check_point_add_BT.setOnClickListener {
                // TODO dodaje punkt do bazy danych i wraca do okna z listą punktów
            }
        }

        // TODO GENERALNIE #2 - TRZEBA ZROBIĆ TAK, ŻE JAK SIĘ SPRAWDZI LOKACJĘ, TO WYŚWIETLA SIĘ OKNO "DODAJ PUNKT", "EDYTUJ PUNKT" - KLIKASZ I SIĘ ROBI JAKAŚ AKCJA - CHOWA SIĘ TO OKNO I WYŚWIETLA OKNO Z PUNKTAMI

        // TODO GENERALNIE TRZEBA ZROBIĆ TAK, ŻE JAK SIĘ ZMIENI X I Y RĘCZNIE, TO WTEDY MA USUWAĆ NAZWĘ I ADRES

        // otwieranie dialogu z listą punktów po zamknięciu dialogu z dodawaniem punktu
        addPointDialog.setOnDismissListener {
            openPointsListDialog(db)
        }

        // otwieranie dialogu z dodawaniem punktu po zamknieciu dialogu ze sprawdzaniem punktu
        checkPointLocationDialog.setOnDismissListener {
            addPointDialog.show()
            pointsButton.visibility = View.VISIBLE
            settingsButton.visibility = View.VISIBLE
            geolocateButton.visibility = View.VISIBLE
        }

    }


    // obsługa przycisku cofania //TODO to na koniec - obsluga przycisku cofania
    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
    }

    // obsługa wyniku prośby o pozwolenie
    override fun onRequestPermissionsResult
                (requestCode: Int,
                 permissions: Array<out String>,
                 grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locateMe()
            } else {
                Toast.makeText(this, R.string.permission_req, Toast.LENGTH_LONG).show()
            }
        }
    }

    // funkcja pobiera aktualną lokalizację i przesuwa na nią widok mapy
    private fun locateMe() {
        fusedLocationClient.lastLocation.addOnSuccessListener(this) {location ->
            mMap.isMyLocationEnabled = true

            if (location != null) {
                lastLocation = location
                val currentLocation = LatLng(location.latitude, location.longitude)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
            }
        }
    }

    private fun generateMarkers(pointsArray: ArrayList<MyPoint>) {

        // pętla iterująca po wszystkich punktach
        for ((index, point) in pointsArray.withIndex()) {
            val position = LatLng(point.latitude, point.longitude)
            val title = if (point.name == "") {
                "Punkty ${index + 1}"
            } else {
                point.name
            }

            // generowanie markerów na podstawie właściwości punktu
            when {
                point.type == "midPoint" -> {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.mid_point_marker)
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 25, 25, false)
                    addMyMarker(position, title, scaledBitmap)
                }
                point.type == "startPoint" -> {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.start_point_marker)
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, false)
                    addMyMarker(position, title, scaledBitmap)
                }
                point.type == "endPoint" -> {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.end_point_marker)
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, false)
                    addMyMarker(position, title, scaledBitmap)
                }
            }
        }
    }

    // dodaj marker do mapy
    private fun addMyMarker(position: LatLng, title: String, icon: Bitmap) {
        mMap.addMarker(MarkerOptions()
            .position(position)
            .title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(icon)))
    }

    // wyświetl dialog z listą punktów
    private fun openPointsListDialog(db: SQLiteDatabase) {
        pointsListDialong.setContentView(R.layout.points_menu)
        pointsListDialong.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pointsListDialong.show()

        // lista znaczników
        val markersRV = pointsListDialong.findViewById<RecyclerView>(R.id.markersListRV)
        markersRV.layoutManager = LinearLayoutManager(this)
        markersRV.adapter = MyAdapter(db)
    }

    // wyświetl dialog z dodawaniem punktu
    private fun openAddPointDialog() {
        addPointDialog.setContentView(R.layout.add_point_menu)
        addPointDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pointsListDialong.dismiss()
        addPointDialog.show()

        search()
    }

    // wyświetl dialog ze sprawdzaniem punktu na mapie
    private fun openCheckPointLocationDialog() {
        checkPointLocationDialog.setContentView(R.layout.check_point_location_options)
        checkPointLocationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val displayedText: String = if (addPointDialog.ownLocationName.text.toString().isEmpty()) {
            addPointDialog.locationName.text.toString()
        } else {
            addPointDialog.ownLocationName.text.toString() + " (" +
                    addPointDialog.locationName.text.toString() + ")"
        }
        checkPointLocationDialog.check_point_loc_name_TV.text = displayedText


        checkPointLocationDialog.show()
        addPointDialog.hide()

        // zmiana pozycji okna
        val window = checkPointLocationDialog.window
        val windowParam = window?.attributes
        windowParam?.gravity = Gravity.BOTTOM
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.attributes = windowParam

    }

    // nadpisanie przycisku do wyszukiwania lokalizacji
    private fun search() {

        val searchText = addPointDialog.findViewById<EditText>(R.id.locationName)

        searchText.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.action == KeyEvent.ACTION_DOWN ||
                    event.action == KeyEvent.KEYCODE_ENTER) {

                findLocation(searchText)

                true
            } else {
                false
            }
        }
    }

    // wyszukanie lokalizacji na podstawie nazwy podanej przez użytkownika
    private fun findLocation(searchText: EditText) {
        val locationName = searchText.text.toString()
        val geocoder: Geocoder = Geocoder(this)
        var addressList: List<Address> = ArrayList()

        val locationLatitude = addPointDialog.findViewById<TextView>(R.id.locationLat)
        val locationLongitude = addPointDialog.findViewById<TextView>(R.id.locationLon)

        try {
            addressList = geocoder.getFromLocationName(locationName, 1)
        } catch (e: IOException) {
            Log.e("findLocation", "findLocation: " + e.message)
        }

        if (addressList.isNotEmpty()) {
            val address: Address = addressList[0]

            Log.e("findLocation", "MAMY!!! $address")
            Log.e("findLocation", "latitude: " + address.latitude.toString())

            locationLatitude.text = address.latitude.toString()
            locationLongitude.text = address.longitude.toString()
        } else {
            Toast.makeText(applicationContext, "Nie udało się ustalić lokalizacji", Toast.LENGTH_SHORT).show()

            locationLatitude.text = ""
            locationLongitude.text = ""
        }
    }

    // sprawdzanie lokalizacji przed dodaniem punktu
    private fun checkLocation() {
        val locationX = addPointDialog.findViewById<TextView>(R.id.locationLat).text.toString()
        val locationY = addPointDialog.findViewById<TextView>(R.id.locationLon).text.toString()

        if (locationX.isNotEmpty() && locationY.isNotEmpty()) {
            addPointDialog.hide()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationX.toDouble(), locationY.toDouble()), 12f))
            mMap.setOnMapLoadedCallback {
                openCheckPointLocationDialog()
            }
        }
    }
}
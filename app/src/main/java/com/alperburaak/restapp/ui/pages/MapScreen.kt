package com.alperburaak.restapp.ui.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    val destinationPoint = remember { Point.fromLngLat(35.612263, 40.864102) }

    // Kullanıcı Konumu State'i
    var userLocation by remember { mutableStateOf<Point?>(null) }

    // Harita Kamerası
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(destinationPoint)
            zoom(12.0)
        }
    }

    // İzin Yöneticisi
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // İzin verildi, konumu al
            getCurrentLocation(context) { loc ->
                userLocation = Point.fromLngLat(loc.longitude, loc.latitude)
                // Kamerayı kullanıcıya odakla
                mapViewportState.easeTo(
                    CameraOptions.Builder().center(userLocation).zoom(14.0).build()
                )
            }
        }
    }

    // İlk açılışta izin iste ve konumu al
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            getCurrentLocation(context) { loc ->
                userLocation = Point.fromLngLat(loc.longitude, loc.latitude)
            }
        }
    }

    // Marker İkonu
    val markerBitmap = rememberBitmapFromVector(Icons.Default.LocationOn, Color.Red)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Konum") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // 1. HARİTA
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
            ) {
                // Konum işaretçisini (Puck) aktif et
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        enabled = true
                        locationPuck = createDefault2DPuck(withBearing = true)
                    }
                }

                // Hedef Marker (Suluova)
                if (markerBitmap != null) {
                    PointAnnotation(
                        point = destinationPoint,
                        iconImageBitmap = markerBitmap,
                        iconSize = 2.0
                    )
                }
            }

            // 2. KONTROL BUTONLARI (Sadece GPS ve Zoom)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {

                // GPS (Konuma Git)
                FloatingActionButton(
                    onClick = {
                        userLocation?.let {
                            mapViewportState.easeTo(
                                CameraOptions.Builder().center(it).zoom(15.0).build(),
                                MapAnimationOptions.Builder().duration(500).build()
                            )
                        } ?: scope.launch { snackbarHostState.showSnackbar("Konum bulunamadı.") }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Default.GpsFixed, "Konum")
                }

                // Zoom Kontrolleri
                SmallFloatingActionButton(onClick = {
                    val z = mapViewportState.cameraState.zoom
                    mapViewportState.easeTo(CameraOptions.Builder().zoom(z + 1).build())
                }) { Icon(Icons.Default.Add, "Zoom In") }

                SmallFloatingActionButton(onClick = {
                    val z = mapViewportState.cameraState.zoom
                    mapViewportState.easeTo(CameraOptions.Builder().zoom(z - 1).build())
                }) { Icon(Icons.Default.Remove, "Zoom Out") }
            }
        }
    }
}

// --- YARDIMCI FONKSİYONLAR ---

// 1. Android Konum Servisi
@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, onLocationFound: (Location) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let { onLocationFound(it) }
    }
}

// 2. Bitmap Dönüştürücü
@Composable
fun rememberBitmapFromVector(vector: ImageVector, tintColor: Color): Bitmap? {
    val context = LocalContext.current
    return remember(vector, tintColor) {
        val vectorDrawable = androidx.vectordrawable.graphics.drawable.VectorDrawableCompat.create(
            context.resources,
            android.R.drawable.ic_menu_mylocation,
            null
        )
        vectorDrawable?.let {
            val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            DrawableCompat.setTint(it, tintColor.toArgb())
            it.draw(canvas)
            return@remember bitmap
        }
        null
    }
}
package com.alperburaak.restapp.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alperburaak.restapp.data.remote.model.Restaurant
import com.alperburaak.restapp.data.remote.model.orderModel.Order
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantRequest
import com.alperburaak.restapp.data.remote.model.authModell.AuthState
import com.alperburaak.restapp.ui.auth.AuthViewModel
import com.alperburaak.restapp.ui.order.OrderViewModel
import com.alperburaak.restapp.ui.rest.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    restaurantViewModel: RestaurantViewModel,
    orderViewModel: OrderViewModel,
    onLogout: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val restState by restaurantViewModel.state.collectAsState()
    val orderState by orderViewModel.state.collectAsState()

    // UI Kontrol State'leri
    var showRestaurantSheet by remember { mutableStateOf(false) }
    var isCreatingNew by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showMapScreen by remember { mutableStateOf(false) }


    if (showMapScreen) {
        // Harita açıksa sadece MapScreen'i göster
        MapScreen(
            onBack = { showMapScreen = false }
        )
        return // Dashboard'u çizme
    }

    // 1. Restoranları Getir (API'den liste çeker)
    LaunchedEffect(Unit) {
        restaurantViewModel.getRestaurant()
    }

    // 2. Restoran Listesi Yüklendiğinde TÜMÜNE Abone Ol (YENİ - GLOBAL DİNLEME)
    LaunchedEffect(restState.restaurants) {
        if (restState.restaurants.isNotEmpty()) {
            val allIds = restState.restaurants.map { it.id }
            orderViewModel.startGlobalListening(allIds)
        }
    }

    // 3. Kullanıcı Restoran Seçtiğinde Sadece Listeyi Getir (Dinleme zaten arka planda açık)
    LaunchedEffect(restState.selectedRestaurant) {
        restState.selectedRestaurant?.let { restaurant ->
            // Sadece cache'den veriyi çek ve API'den güncelle, dinleme başlatma (zaten başladı)
            orderViewModel.loadOrdersForRestaurant(restaurant.id)
        }
    }


    // 4. Yeni Sipariş Geldiğinde Snackbar Göster
    var previousOrderCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(orderState.orders.size) {
        // Eğer sipariş sayısı arttıysa ve bu ilk açılış değilse
        if (orderState.orders.size > previousOrderCount && previousOrderCount != 0) {
            snackbarHostState.showSnackbar(
                message = "🔔 Yeni bir siparişiniz var!",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
        previousOrderCount = orderState.orders.size
    }

    // 5. Yetki Kontrolü
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            onLogout()
        }
    }

    // Yeni restoran oluşturulduğunda formu kapat
    LaunchedEffect(restState.created) {
        if (restState.created) {
            isCreatingNew = false
            restaurantViewModel.clearCreatedFlag()
        }
    }

    // Şube Seçim Menüsü (Bottom Sheet)
    if (showRestaurantSheet) {
        ModalBottomSheet(onDismissRequest = { showRestaurantSheet = false }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Şube Değiştir", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                LazyColumn {
                    items(restState.restaurants) { restaurant ->
                        RestaurantSelectionItem(
                            restaurant = restaurant,
                            isSelected = restaurant.id == restState.selectedRestaurant?.id,
                            onClick = {
                                // Eski dinlemeyi durdur, yenisini seç
                                restaurantViewModel.selectRestaurant(restaurant)
                                showRestaurantSheet = false
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showRestaurantSheet = false
                                isCreatingNew = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Yeni Restoran Ekle")
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {

                FloatingActionButton(
                    onClick = { showMapScreen = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Haritayı Aç"
                    )
                }

        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isCreatingNew || restState.restaurants.isEmpty()) {
                        Text("Yeni Restoran")
                    } else {
                        Text(restState.selectedRestaurant?.name ?: "RestApp Panel")
                    }
                },
                actions = {

                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Çıkış Yap", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }


    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                restState.isLoading || orderState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                restState.error != null -> {
                    ErrorView(message = restState.error!!, onRetry = { restaurantViewModel.getRestaurant() })
                }
                isCreatingNew || restState.restaurants.isEmpty() -> {
                    CreateRestaurantView(
                        onSubmit = { request -> restaurantViewModel.createRestaurant(request) },
                        onCancel = if (restState.restaurants.isNotEmpty()) { { isCreatingNew = false } } else null
                    )
                }
                restState.restaurants.isNotEmpty() && restState.selectedRestaurant != null -> {
                    val currentRestaurantId = restState.selectedRestaurant!!.id
                    val filteredOrders = orderState.orders.filter {

                        it.restaurant_id == currentRestaurantId
                    }
                    DashboardView(
                        restaurant = restState.selectedRestaurant!!,
                        orders = orderState.orders,
                        onChangeRestaurantClick = { showRestaurantSheet = true },
                        onAcceptOrder = { uniqueCode -> orderViewModel.accept(uniqueCode) },
                        onRejectOrder = { uniqueCode -> orderViewModel.reject(uniqueCode) }
                    )
                }
            }
        }
    }
}

// --- DASHBOARD (ANA EKRAN) ---
@Composable
fun DashboardView(
    restaurant: Restaurant,
    orders: List<Order>,
    onChangeRestaurantClick: () -> Unit,
    onAcceptOrder: (String) -> Unit,
    onRejectOrder: (String) -> Unit
) {
    // İstatistikler
    val pendingCount = orders.count { it.order_details.status == "pending" }
    val completedCount = orders.count { it.order_details.status == "delivered" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hoşgeldin Kartı
        item {
            WelcomeCard(userName = restaurant.name, onClick = onChangeRestaurantClick)
        }

        // 2. İstatistik Kartları
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(Modifier.weight(1f), "Bekleyen", Icons.Default.PendingActions, pendingCount.toString())
                DashboardCard(Modifier.weight(1f), "Tamamlanan", Icons.Default.CheckCircle, completedCount.toString())
            }
        }

        // 3. Restoran Bilgisi
        item {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = restaurant.physical_address,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }

        // 4. Sipariş Listesi Başlığı
        item {
            Text(
                "Aktif Siparişler",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 5. Siparişler
        if (orders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Henüz sipariş yok.", color = Color.Gray)
                }
            }
        } else {
            // "pending" olanları en üste al, diğerlerini tarihe göre sırala
            val sortedOrders = orders.sortedWith(compareBy<Order> { it.order_details.status != "pending" }.thenByDescending { it.created_at })

            items(sortedOrders) { order ->
                OrderItemCard(
                    order = order,
                    onAccept = { onAcceptOrder(order.unique_code) },
                    onReject = { onRejectOrder(order.unique_code) }
                )
            }
        }
    }
}

// --- SİPARİŞ KARTI ---
@Composable
fun OrderItemCard(
    order: Order,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.customer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#${order.unique_code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₺${order.order_details.final_amount}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    OrderStatusBadge(status = order.order_details.status)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            // Ürünler
            order.items.take(3).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${item.quantity}x ${item.product_name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "₺${item.total}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (order.items.size > 3) {
                Text(
                    text = "+${order.items.size - 3} ürün daha...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Not Varsa
            if (!order.order_details.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Not: ${order.order_details.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Aksiyon Butonları (Sadece 'pending' ise)
            if (order.order_details.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reddet")
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Onayla")
                    }
                }
            }
        }
    }
}

// --- RENKLİ DURUM ROZETİ ---
@Composable
fun OrderStatusBadge(status: String) {
    val (color, text) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFA000) to "Bekliyor"
        "preparing" -> Color(0xFF1976D2) to "Hazırlanıyor"
        "ready" -> Color(0xFF0288D1) to "Hazır"
        "delivered" -> Color(0xFF388E3C) to "Tamamlandı"
        "rejected", "cancelled" -> Color(0xFFD32F2F) to "İptal"
        "accepted" -> Color(0xFF2E7D32) to "Onaylandı"
        else -> Color.Gray to status
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// --- RESTORAN OLUŞTURMA (DİNAMİK) ---
@Composable
fun CreateRestaurantView(
    onSubmit: (CreateRestaurantRequest) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Restoranını Oluştur", style = MaterialTheme.typography.headlineSmall)
            Text("Lütfen işletme bilgilerinizi giriniz.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
        }

        item {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("İşletme Adı") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Açıklama") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Fiziksel Adres") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-Posta") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(24.dp))
        }

        item {
            Button(
                onClick = {
                    val request = CreateRestaurantRequest(
                        name = name, description = desc, physical_address = address, phone = phone, email = email,
                        city = "Istanbul", country = "Turkey", main_language = "tr", support_menu_lnaguage_ids = "",
                        operation_start_time = "09:00", operation_end_time = "22:00",
                        city_id = "34", district_id = "1641", neighborhood_id = "", logo = null
                    )
                    onSubmit(request)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && email.isNotEmpty()
            ) {
                Text("İşletmeyi Kaydet")
            }
            if (onCancel != null) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Vazgeç") }
            }
        }
    }
}

// --- DİĞER YARDIMCI BİLEŞENLER ---
@Composable
fun WelcomeCard(userName: String, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Hoş Geldin,", style = MaterialTheme.typography.labelLarge)
                Text(userName, style = MaterialTheme.typography.headlineSmall)
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Değiştir")
        }
    }
}

@Composable
fun RestaurantSelectionItem(restaurant: Restaurant, isSelected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = restaurant.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun DashboardCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, count: String) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(8.dp))
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Tekrar Dene") }
    }


}
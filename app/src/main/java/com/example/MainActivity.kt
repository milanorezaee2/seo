package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SeoViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SeoMasterApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeoMasterApp(viewModel: SeoViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Emerald700.copy(alpha = 0.2f))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "سئو مستر پرو",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "SEO Master Pro Suite",
                        style = MaterialTheme.typography.labelSmall.copy(color = Emerald500)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "جعبه ابزار جامع بهینه‌سازی و سئو سایت",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                Spacer(modifier = Modifier.height(8.dp))

                AppTab.values().forEach { tab ->
                    val isSelected = tab == currentTab
                    val icon = when (tab) {
                        AppTab.HOME -> Icons.Default.Dashboard
                        AppTab.SITE_AUDIT -> Icons.Default.Analytics
                        AppTab.CORE_WEB_VITALS -> Icons.Default.Speed
                        AppTab.COMPETITOR -> Icons.Default.CompareArrows
                        AppTab.SCHEMA_GEN -> Icons.Default.Code
                        AppTab.SITEMAP_GEN -> Icons.Default.Map
                        AppTab.DNS_LOOKUP -> Icons.Default.Dns
                        AppTab.ROBOTS_TXT -> Icons.Default.SmartToy
                        AppTab.HISTORY -> Icons.Default.History
                    }

                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) Emerald500 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Emerald500 else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            viewModel.selectTab(tab)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("drawer_item_${tab.name}"),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Emerald500.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentTab.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            if (currentTab != AppTab.HOME) {
                                Text(
                                    text = "SEO Master Pro",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Emerald500,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("open_drawer_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "منو ابزارها",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        if (currentTab != AppTab.HOME) {
                            IconButton(onClick = { viewModel.selectTab(AppTab.HOME) }) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "داشبورد",
                                    tint = Emerald500
                                )
                            }
                        } else {
                            IconButton(onClick = { viewModel.selectTab(AppTab.HISTORY) }) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "تاریخچه",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    val bottomTabs = listOf(
                        AppTab.HOME to Icons.Default.Dashboard,
                        AppTab.SITE_AUDIT to Icons.Default.Analytics,
                        AppTab.CORE_WEB_VITALS to Icons.Default.Speed,
                        AppTab.COMPETITOR to Icons.Default.CompareArrows,
                        AppTab.SCHEMA_GEN to Icons.Default.Code
                    )

                    bottomTabs.forEach { (tab, icon) ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Emerald500,
                                selectedTextColor = Emerald500,
                                indicatorColor = Emerald500.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_bottom_${tab.name}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        AppTab.HOME -> HomeScreen(viewModel = viewModel)
                        AppTab.SITE_AUDIT -> SiteAuditScreen(viewModel = viewModel)
                        AppTab.CORE_WEB_VITALS -> CoreWebVitalsScreen(viewModel = viewModel)
                        AppTab.COMPETITOR -> CompetitorScreen(viewModel = viewModel)
                        AppTab.SCHEMA_GEN -> SchemaGeneratorScreen(viewModel = viewModel)
                        AppTab.SITEMAP_GEN -> SitemapGeneratorScreen(viewModel = viewModel)
                        AppTab.DNS_LOOKUP -> DnsLookupScreen(viewModel = viewModel)
                        AppTab.ROBOTS_TXT -> RobotsTxtScreen(viewModel = viewModel)
                        AppTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

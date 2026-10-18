package com.smartwashcare.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartwashcare.app.ui.screens.AnalyzingScreen
import com.smartwashcare.app.ui.screens.CameraScreen
import com.smartwashcare.app.ui.screens.HomeScreen
import com.smartwashcare.app.ui.screens.LabelStartScreen
import com.smartwashcare.app.ui.screens.LabelViewModel
import com.smartwashcare.app.ui.screens.LoginScreen
import com.smartwashcare.app.ui.screens.PlaceholderScreen
import com.smartwashcare.app.ui.screens.SplashScreen

private data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomTabs = listOf(
    BottomTab(Routes.HOME, "홈", Icons.Outlined.Home),
    BottomTab(Routes.GROUP_HOME, "세탁 그룹", Icons.Filled.LocalLaundryService),
    BottomTab(Routes.CLOSET, "내 옷장", Icons.Filled.Checkroom)
)

// 하단 탭바를 보여줄 화면들
private val tabBarRoutes = setOf(
    Routes.HOME, Routes.GROUP_HOME, Routes.CLOSET, Routes.CLOSET_LIST
)

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 라벨 분석 흐름에서 촬영한 사진을 화면끼리 공유
    val labelVm: LabelViewModel = viewModel()

    val goBack: () -> Unit = {
        nav.popBackStack()
        Unit
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in tabBarRoutes) {
                AppBottomBar(nav = nav, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        nav.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onLabelAnalysis = { nav.navigate(Routes.LABEL_START) },
                    onWashGroup = { switchTab(nav, Routes.GROUP_HOME) },
                    onCloset = { switchTab(nav, Routes.CLOSET) },
                    onSeeAll = { nav.navigate(Routes.CLOSET_LIST) }
                )
            }

            // ── 라벨 분석 ──
            composable(Routes.LABEL_START) {
                LabelStartScreen(
                    onBack = goBack,
                    onStartCamera = { nav.navigate(Routes.CAMERA) }
                )
            }
            composable(Routes.CAMERA) {
                CameraScreen(
                    onClose = goBack,
                    onPhotoReady = { uri ->
                        labelVm.photoUri = uri
                        nav.navigate(Routes.ANALYZING)
                    }
                )
            }
            composable(Routes.ANALYZING) {
                AnalyzingScreen(
                    onBack = goBack,
                    onDone = {
                        // 촬영/분석 화면은 뒤로가기 기록에서 지우고 결과 화면으로
                        nav.navigate(Routes.RESULT) {
                            popUpTo(Routes.LABEL_START)
                        }
                    }
                )
            }
            composable(Routes.RESULT) {
                PlaceholderScreen("분석 결과", listOf(
                    "정보 확인하고 수정하기" to { nav.navigate(Routes.RESULT_EDIT) }
                ))
            }
            composable(Routes.RESULT_EDIT) {
                PlaceholderScreen("의류 정보 확인", listOf(
                    "확인하고 옷장에 저장" to {
                        nav.navigate(Routes.SAVE_DONE) {
                            popUpTo(Routes.HOME)
                        }
                    }
                ))
            }
            composable(Routes.SAVE_DONE) {
                PlaceholderScreen("저장 완료", listOf(
                    "다른 라벨도 분석하기" to {
                        nav.navigate(Routes.LABEL_START) { popUpTo(Routes.HOME) }
                    },
                    "내 옷장으로 가기" to {
                        nav.navigate(Routes.CLOSET) { popUpTo(Routes.HOME) }
                    }
                ))
            }

            // ── 세탁 그룹 ──
            composable(Routes.GROUP_HOME) {
                PlaceholderScreen("세탁 그룹", listOf(
                    "새 세탁 그룹 만들기" to { nav.navigate(Routes.GROUP_SELECT) },
                    "저장한 그룹 보기" to { nav.navigate(Routes.GROUP_DETAIL) }
                ))
            }
            composable(Routes.GROUP_SELECT) {
                PlaceholderScreen("의류 선택", listOf(
                    "선택한 옷 분석하기" to { nav.navigate(Routes.GROUP_ANALYZE) }
                ))
            }
            composable(Routes.GROUP_ANALYZE) {
                PlaceholderScreen("그룹 분석 중", listOf(
                    "분석 완료 (임시)" to { nav.navigate(Routes.GROUP_RESULT) }
                ))
            }
            composable(Routes.GROUP_RESULT) {
                PlaceholderScreen("그룹 추천", listOf(
                    "그룹 상세 보기" to { nav.navigate(Routes.GROUP_DETAIL) }
                ))
            }
            composable(Routes.GROUP_DETAIL) {
                PlaceholderScreen("그룹 상세", listOf(
                    "이 조건으로 그룹 저장" to {
                        nav.navigate(Routes.GROUP_HOME) { popUpTo(Routes.HOME) }
                    }
                ))
            }

            // ── 내 옷장 ──
            composable(Routes.CLOSET) {
                PlaceholderScreen("내 옷장", listOf(
                    "전체 의류 보기" to { nav.navigate(Routes.CLOSET_LIST) }
                ))
            }
            composable(Routes.CLOSET_LIST) {
                PlaceholderScreen("전체 의류", listOf(
                    "의류 상세 보기" to { nav.navigate(Routes.CLOSET_DETAIL) }
                ))
            }
            composable(Routes.CLOSET_DETAIL) {
                PlaceholderScreen("의류 상세", listOf(
                    "의류 정보 수정" to { nav.navigate(Routes.CLOSET_EDIT) }
                ))
            }
            composable(Routes.CLOSET_EDIT) {
                PlaceholderScreen("의류 정보 수정", listOf(
                    "변경 내용 저장" to goBack
                ))
            }
        }
    }
}

@Composable
private fun AppBottomBar(nav: NavHostController, currentRoute: String?) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        bottomTabs.forEach { tab ->
            val selected = currentRoute == tab.route ||
                    (tab.route == Routes.CLOSET && currentRoute == Routes.CLOSET_LIST)
            NavigationBarItem(
                selected = selected,
                onClick = { switchTab(nav, tab.route) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) }
            )
        }
    }
}

// 탭 이동: 탭끼리 이동해도 뒤로가기 기록이 쌓이지 않게 처리
private fun switchTab(nav: NavHostController, route: String) {
    nav.navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
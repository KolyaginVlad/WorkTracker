package ru.kolyagin.worktracker.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkTrackerTheme {
                rememberSystemUiController().setStatusBarColor(MaterialTheme.colors.primaryVariant)
                val navController = rememberNavController()

                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    skipHalfExpanded = true
                )
                val modalBottomNavigator = remember { BottomSheetNavigator(sheetState) }
                navController.navigatorProvider += modalBottomNavigator
                ModalBottomSheetLayout(
                    bottomSheetNavigator = modalBottomNavigator,
                    sheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        DestinationsNavHost(
                            modifier = Modifier
                                .fillMaxSize(),
                            navController = navController,
                            navGraph = NavGraphs.root,
                            engine = rememberAnimatedNavHostEngine()
                        )
                    }
                }
            }
        }
    }
}
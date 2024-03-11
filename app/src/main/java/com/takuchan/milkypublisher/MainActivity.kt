package com.takuchan.milkypublisher


import android.annotation.SuppressLint
import android.content.pm.PackageManager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.takuchan.milkypublisher.compose.BluetoothSettingScreen
import com.takuchan.milkypublisher.compose.ConnectingScreen

import com.takuchan.milkypublisher.compose.HomeScreen
import com.takuchan.milkypublisher.compose.MainScreen
import com.takuchan.milkypublisher.compose.RobotControllerScreen
import com.takuchan.milkypublisher.preference.UDPController
import com.takuchan.milkypublisher.repository.ReceiveUdpRepository

import com.takuchan.milkypublisher.ui.theme.MilkyPublisherTheme
import com.takuchan.milkypublisher.viewmodel.DetectBluetoothList
import com.takuchan.milkypublisher.viewmodel.DetectState
import com.takuchan.milkypublisher.viewmodel.UDPFlowViewModel


import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

    private val viewModel: DetectState by viewModels()
    private lateinit var cameraExecutor: ExecutorService
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        val blViewModel = ViewModelProvider(this)[DetectBluetoothList::class.java]
        val udpViewModel = ViewModelProvider(this)[UDPFlowViewModel::class.java]


        if(packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
//            Toast.makeText(applicationContext,"Bluetooth使える",Toast.LENGTH_SHORT).show()
        }

        setContent {
            MilkyPublisherTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MilkyPublisherApp(detectStateViewModel = viewModel,executorService = cameraExecutor,blViewModel = blViewModel,udpViewModel=udpViewModel)
                    }
            }
        }
    }

}

@Composable
fun MilkyPublisherApp(detectStateViewModel: DetectState,
                      executorService: ExecutorService,
                      blViewModel: DetectBluetoothList,
                      udpViewModel: UDPFlowViewModel
                      ){
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    udpViewModel.receiveUDP.observe(lifecycleOwner, Observer { newData->
        Log.d("UDPしたよ",newData)
    })
    MilkyPublisherNavHost(
        navController = navController,
        detectStateViewModel = detectStateViewModel,
        cameraExecutorService = executorService,
        blViewModel = blViewModel,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MilkyPublisherNavHost(
    navController: NavHostController,
    detectStateViewModel: DetectState,
    cameraExecutorService: ExecutorService,
    blViewModel:DetectBluetoothList,
){

Column {
    NavHost(navController = navController, startDestination = "home"){
        composable("home") {
            MainScreen(
                navMainController = navController,
                detectState = detectStateViewModel,
                cameraExecutorService = cameraExecutorService,
                blViewModel = blViewModel,
                toBluetoothSettingButton = {
                    navController.navigate("wifiSetting")
                })
        }
        composable("bluetoothSetting"){
            BluetoothSettingScreen(navController = navController, blViewModel =blViewModel)
        }
        composable("wifiSetting"){
            ConnectingScreen({
                navController.popBackStack()
            },navController = navController)
        }
    }


}



}
package com.example.wear_test_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.wear_test_1.theme.Wear_test_1Theme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

// リファレンス -> https://developer.android.com/training/wearables/data/data-items?hl=ja

private const val FROM_WEAR_PATH = "/vital"
private const val COUNT_KEY = "com.example.wear_test_1.count"

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MobileApp() }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("FUZ", "activity dataEvents : $dataEvents")
        dataEvents.forEach { it ->
            val uri = it.dataItem.uri
            val nodeId = uri.host
            val payload = uri.toString().toByteArray()
            Log.d("FUZ", "uri : $uri")
            Log.d("FUZ", "nodeID : $nodeId")
            Log.d("FUZ", "payload : $payload")
            it.dataItem.also { item ->
                if ((item.uri.path?.compareTo(FROM_WEAR_PATH) ?: -1) == 0) {
                    DataMapItem.fromDataItem(item).dataMap.apply {
                        updateCount(getInt(COUNT_KEY))
                    }
                }
            }
            // Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCount(count: Int) {
        Log.d("FUZ", "update : $count")
        Toast.makeText(applicationContext, count.toString(), Toast.LENGTH_SHORT).show()
    }

    public override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    public override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }
}

//class MyWearableListenerService: WearableListenerService() {
//    companion object {
//        private const val FROM_WEAR_PATH = "/vital"
//    }
//
//    override fun onDataChanged(dataEvents: DataEventBuffer) {
//        super.onDataChanged(dataEvents)
//        Log.d("FUZ", "dataEvents : $dataEvents")
//        dataEvents.forEach { it ->
//            val uri = it.dataItem.uri
//            val nodeId = uri.host
//            val payload = uri.toString().toByteArray()
//            Log.d("FUZ", "uri : $uri")
//            Log.d("FUZ", "nodeID : $nodeId")
//            Log.d("FUZ", "payload : $payload")
//            Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_SHORT).show()
//        }
//    }
//}

@Composable
fun MobileApp() {
    Wear_test_1Theme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hello Wear", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}
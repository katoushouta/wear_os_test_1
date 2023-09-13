/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.wear_test_1.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.rememberScalingLazyListState
import com.example.wear_test_1.R
import com.example.wear_test_1.presentation.theme.Wear_test_1Theme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch

private const val TO_PHONE_PATH = "/vital"
private const val COUNT_KEY = "com.example.wear_test_1.count"

class MainActivity : ComponentActivity() {
    private lateinit var dataClient: DataClient
    private var count = 0

    @SuppressLint("VisibleForTests")
    private fun updateCounter(dispatcher: (prevCount: Int) -> Int) {
        count = dispatcher(count)
        val putDataReq = PutDataMapRequest.create(TO_PHONE_PATH).run {
            dataMap.putInt(COUNT_KEY, count)
            asPutDataRequest()
        }
        val putDataTask = dataClient.putDataItem(putDataReq)

        putDataTask.addOnCompleteListener { task ->
            Log.d("FUZ", "task : $task")
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "同期しました : $count", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "同期に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataClient = Wearable.getDataClient(applicationContext)
        setContent {
            Scaffold(
                timeText = { TimeText() }
            ) {
                WearApp("Android", ::updateCounter)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WearApp(greetingName: String, increaseCounter: (dispatcher: (prevCount: Int) -> Int) -> Unit) {
    val listState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequster = remember {FocusRequester()}

    Wear_test_1Theme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .onRotaryScrollEvent {
                    Log.d("scroll", "val : ${it.verticalScrollPixels}")
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequster)
                .focusable(),
            state = listState,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            item {
                Greeting(greetingName = greetingName)
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    increaseCounter {
                        it + 1
                    }
                }) {
                    Text("+", Modifier.padding(all = 16.dp))
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                    increaseCounter {
                        0
                    }
                }) {
                    Text("C", Modifier.padding(all = 16.dp))
                }
            }
        }
        LaunchedEffect(key1 = Unit) {
            focusRequster.requestFocus()
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android") {
        print("preview")
    }
}
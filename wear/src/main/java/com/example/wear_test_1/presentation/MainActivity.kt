/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.wear_test_1.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tiles.LayoutElementBuilders.HorizontalAlignment
import com.example.wear_test_1.R
import com.example.wear_test_1.presentation.theme.Wear_test_1Theme
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlin.reflect.KFunction1

class MainActivity : ComponentActivity() {
    private var nodeSet: MutableSet<Node>? = null

    companion object {
        private const val WEAR_CAPABILITY_NAME = "wear_capability"
        private const val TO_WATCH_PATH = "/vital"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val capabilityInfoTask: Task<CapabilityInfo> =
            Wearable.getCapabilityClient(applicationContext).getCapability(
                WEAR_CAPABILITY_NAME,
                CapabilityClient.FILTER_REACHABLE
            )

        capabilityInfoTask.addOnCompleteListener { task ->
            Log.d("FUZ", "capability connect")
            if (task.isSuccessful) {
                Log.d("FUZ", "capability connect success")
                nodeSet = task.result?.nodes
            } else {
                Log.d("FUZ", "capability connect failed")
                Toast.makeText(applicationContext, "送信先が見つかりません", Toast.LENGTH_SHORT).show()
            }
        }

        fun sentWatchMessage(message: String) {
            Log.d("FUZ","send : $message")
            nodeSet?.let { node ->
                Log.d("FUZ", "nodeSet")
                pickBestNodeId(node)?.let {
                    Log.d("FUZ", "pick : $it")
                    val data = message.toByteArray(Charsets.UTF_8)
                    Log.d("FUZ", "node : $node")
                    Wearable.getMessageClient(applicationContext)
                        .sendMessage(it, TO_WATCH_PATH, data)
                        .apply {
                            addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext,
                                    message + "を送信しました",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            addOnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "送信に失敗しました",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        setContent {
            WearApp("Android", ::sentWatchMessage)
        }
    }

    private fun pickBestNodeId(nodes: Set<Node>): String? {
        Log.d("FUZ", "node ${nodes.size}")
        return nodes.firstOrNull { it.isNearby }?.id ?: nodes.firstOrNull()?.id
    }
}

@Composable
fun WearApp(greetingName: String, sendMessage: (text: String) -> Unit) {
    Wear_test_1Theme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting(greetingName = greetingName)
            Button(onClick = {
                sendMessage("めっせーじワン")
            }) {
                Text("SEND MES")
            }
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
    WearApp("Preview Android") { text ->
        print(text)
    }
}
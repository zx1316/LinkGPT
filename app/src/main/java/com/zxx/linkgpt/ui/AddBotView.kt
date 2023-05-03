package com.zxx.linkgpt.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zxx.linkgpt.R
import com.zxx.linkgpt.ui.util.ShowErrorDialog
import com.zxx.linkgpt.ui.util.tryReadBitmap
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import java.io.ByteArrayOutputStream
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

@Composable
fun AddBot(
    vm: LinkGPTViewModel,
    onClickBack: () -> Unit
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var settings by rememberSaveable { mutableStateOf("") }
    var temperature by rememberSaveable { mutableStateOf(1.0F) }
    var topP by rememberSaveable { mutableStateOf(1.0F) }
    var presencePenalty by rememberSaveable { mutableStateOf(0.0F) }
    var frequencyPenalty by rememberSaveable { mutableStateOf(0.0F) }
    var expandAdvanced by rememberSaveable { mutableStateOf(false) }
    var showError by rememberSaveable { mutableStateOf(false) }
    var uri by rememberSaveable { mutableStateOf(Uri.EMPTY) }
    var errorDetail by rememberSaveable { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { if (it.resultCode == Activity.RESULT_OK) uri = it.data?.data as Uri }
    )
    val example1 = stringResource(id =  R.string.settings_example1)
    val example2 = stringResource(id =  R.string.settings_example2)
    val nameEmpty = stringResource(id = R.string.bot_name_empty)
    val nameUser = stringResource(id = R.string.bot_name_user)
    val nameAlready = stringResource(id = R.string.bot_name_already)
    val mayDecrease = stringResource(id = R.string.may_decrease_quality)
    val notRecommend = stringResource(id = R.string.change_both_not_recommended)

    if (showError) {
        ShowErrorDialog(detail = errorDetail, callback = { showError = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colors.primaryVariant,
                contentColor = Color.White,
                title = { Text(text = stringResource(id = R.string.add_bot)) },
                navigationIcon = {
                    IconButton(
                        onClick = onClickBack,
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                                contentDescription = null
                            )
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            if ("" == name) {
                                errorDetail = nameEmpty
                                showError = true
                            } else if ("user" == name) {
                                errorDetail = nameUser
                                showError = true
                            } else {
                                for (bot in vm.botList.value) {
                                    if (bot.name == name) {
                                        errorDetail = String.format(nameAlready, name)
                                        showError = true
                                        break
                                    }
                                }
                                if (!showError) {
                                    val bitmap = tryReadBitmap(context.contentResolver, uri)
                                    if (bitmap != null) {
                                        val byteArrayOutputStream = ByteArrayOutputStream()
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                        byteArrayOutputStream.close()
                                        context.openFileOutput("$name.png", Context.MODE_PRIVATE).use {
                                            it.write(byteArrayOutputStream.toByteArray())
                                        }
                                    }
                                    vm.addBot(
                                        name = name,
                                        settings = settings,
                                        temperature = rounding(temperature),
                                        topP = rounding(topP),
                                        presencePenalty = rounding(presencePenalty),
                                        frequencyPenalty = rounding(frequencyPenalty)
                                    )
                                    onClickBack()
                                }
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_check_24),
                                tint = Color.White,
                                contentDescription = null
                            )
                        }
                    )
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = stringResource(id = R.string.bot_name))
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = stringResource(id = R.string.avatar))
                        Row {
                            val imageModifier = Modifier
                                .size(128.dp)
                                .clip(RoundedCornerShape(100))
                            if (Uri.EMPTY.equals(uri)) {
                                Image(
                                    painter = painterResource(id = R.drawable.default_bot),
                                    contentDescription = null,
                                    modifier = imageModifier
                                )
                            } else {
                                tryReadBitmap(context.contentResolver, uri)?.asImageBitmap()?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        modifier = imageModifier,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(64.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.height(128.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_PICK)
                                        intent.type = "image/*"
                                        launcher.launch(intent)
                                    },
                                    content = { Text(text = stringResource(id = R.string.choose_avatar)) }
                                )
                                Button(
                                    onClick = { uri = Uri.EMPTY },
                                    content = { Text(text = stringResource(id = R.string.default_avatar)) }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = stringResource(id = R.string.settings))
                        TextField(
                            value = settings,
                            onValueChange = { settings = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(192.dp),
                            placeholder = { Text(text = stringResource(id = R.string.settings_placeholder)) }
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { settings = example1 },
                                content = { Text(text = stringResource(id = R.string.settings_example_button1)) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { settings = example2 },
                                content = { Text(text = stringResource(id = R.string.settings_example_button2)) }
                            )
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.advanced_parameters))
                        IconButton(
                            onClick = {
                                expandAdvanced = !expandAdvanced
                                if (!expandAdvanced) {
                                    temperature = 1.0F
                                    topP = 1.0F
                                    presencePenalty = 0.0F
                                    frequencyPenalty = 0.0F
                                }
                            },
                            content = {
                                Icon(
                                    painter = painterResource(id = if (expandAdvanced) R.drawable.baseline_expand_less_24 else R.drawable.baseline_expand_more_24),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
                // todo: add animation
                if (expandAdvanced) {
                    item {
                        Column {
                            ParaAdjust(
                                paraName = stringResource(id = R.string.temperature),
                                value = temperature,
                                range = 0.0F..2.0F,
                                callback = { temperature = it },
                                alert = { if (temperature >= 1.505F) mayDecrease else null }
                            )

                            ParaAdjust(
                                paraName = stringResource(id = R.string.top_p),
                                value = topP,
                                range = 0.0F..1.0F,
                                callback = { topP = it },
                                alert = { if (abs(temperature - 1.0F) >= 0.005F && 1.0F - topP >= 0.005F) notRecommend else null }
                            )

                            ParaAdjust(
                                paraName = stringResource(id = R.string.presence_penalty),
                                value = presencePenalty,
                                range = -2.0F..2.0F,
                                callback = { presencePenalty = it },
                                alert = { if (presencePenalty >= 1.005F || presencePenalty <= -0.005F) mayDecrease else null }
                            )

                            ParaAdjust(
                                paraName = stringResource(id = R.string.frequency_penalty),
                                value = frequencyPenalty,
                                range = -2.0F..2.0F,
                                callback = { frequencyPenalty = it },
                                alert = { if (frequencyPenalty >= 1.005F || frequencyPenalty <= -0.005F) mayDecrease else null }
                            )

                            Text(
                                text = stringResource(id = R.string.para_info),
                                style = typography.body2.copy(color = Color.Gray),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ParaAdjust(
    paraName: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    callback: (Float) -> Unit,
    alert: () -> String?
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = paraName, modifier = Modifier.width(120.dp))
            alert()?.let {
                Image(
                    painter = painterResource(id = R.drawable.baseline_warning_20),
                    contentDescription = null
                )
                Text(
                    text = it,
                    style = typography.body2.copy(color = Color.Gray)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = value,
                onValueChange = callback,
                valueRange = range,
                modifier = Modifier.weight(1.0F)
            )
            Text(
                text = String.format(if (value < 0) "%.2f" else " %.2f", value),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(42.dp)
            )
        }
    }
}

fun rounding(x: Float): Float {
    return sign(x) * floor(abs(x) * 100F + 0.5F) * 0.01F
}

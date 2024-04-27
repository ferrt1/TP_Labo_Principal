package com.example.cypher_vault.view.resources

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.view.login.firstColor
import com.example.cypher_vault.view.login.secondColor

@Composable
fun CustomTitle() {
    val textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 54.sp)

    Row(modifier = Modifier.wrapContentSize().offset(16.dp).padding( top = 40.dp), )
    {
        Column() {
            Row (
                horizontalArrangement = Arrangement.Absolute.Left
            ){
                Text(
                    text = "C",
                    color = firstColor,
                    style = textStyle,
                )
                Text(
                    text = "ypher",
                    color = secondColor,
                    style = textStyle,
                )
            }
            Row {
                Text(
                    text = "V",
                    color = firstColor,
                    style = textStyle,
                )
                Text(
                    text = "ault",
                    color = secondColor,
                    style = textStyle,
                )
            }
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(-(32).dp.roundToPx(), 14.dp.roundToPx()) }
                .size(94.dp)
                .align(Alignment.Bottom)
                .border(2.dp, firstColor, RoundedCornerShape(50))
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(82.dp)
                    .align(Alignment.Center)
            )
        }
    }

}
package pe.breaker.dkaviplay.presentation.screen.reglas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun CardClubDKAVI(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ColorPrimaryBorder, RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorPrimary.copy(alpha = 0.3f),
                        ColorBackgroundDark
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "OFICIAL",
                style = TextStyleTag,
                modifier = Modifier.padding(bottom = 4.dp) // mb-1
            )

            Text(
                text = "Club DKAVI",
                style = TextStyleTitle
            )
        }
    }
}

val ColorBackgroundDark = Color(0xFF121212)
val ColorPrimaryBorder = colorPrimary.copy(alpha = 0.2f)

val TextStyleTag = TextStyle(
    color = colorPrimary,
    fontWeight = FontWeight.Bold,
    letterSpacing = 2.sp, // tracking-widest
    fontSize = 12.sp // text-xs
)

val TextStyleTitle = TextStyle(
    color = Color.White,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp, // text-2xl
    lineHeight = 28.sp // leading-tight (aprox)
)
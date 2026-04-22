package pe.breaker.dkaviplay.presentation.screen.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun NoAccount(
    modifier: Modifier,
    onclick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.fillMaxWidth(0.5f).height(1.dp)
                .background(Color.White.copy(0.1f))
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = buildAnnotatedString {
                append("¿No tienes una cuenta? ")
                withStyle(
                    SpanStyle(
                        color = colorPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    append("Regístrate")
                }
            },
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.clickable { onclick() },
            style = TextStyle(fontSize = 16.sp)
        )
    }
}
package pe.breaker.dkaviplay.presentation.components.dialog
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import pe.breaker.dkaviplay.presentation.animations.animations.levelUp.LevelUpProAnimation
//import pe.breaker.dkaviplay.presentation.util.RankResourceMapper
//
//@Composable
//fun LevelUpDialog(
//    categoriaAnterior: String?,
//    categoria: String?,
//    userImageUrl: String,
//    onDismiss: () -> Unit
//) {
//    if (categoriaAnterior != null && categoria!= null) {
//        Dialog(
//            onDismissRequest = onDismiss,
//            properties = DialogProperties(
//                usePlatformDefaultWidth = false, // ESTO es la clave para el fullscreen
//                dismissOnBackPress = true,
//                dismissOnClickOutside = false
//            )
//        ) {
//            // El Box ocupa toda la pantalla del diálogo
//            Box(modifier = Modifier.fillMaxSize()) {
//                LevelUpProAnimation(
//                    previousLevelName = categoriaAnterior, // Idealmente tendrías el previo
//                    newLevelName = categoria,
//                    userImageUrl = userImageUrl,
//                    oldRankRes = RankResourceMapper.getDrawableByRank(categoriaAnterior),
//                    newRankRes = RankResourceMapper.getDrawableByRank(categoria),
//                    onClose = onDismiss
//                )
//            }
//        }
//    }
//}
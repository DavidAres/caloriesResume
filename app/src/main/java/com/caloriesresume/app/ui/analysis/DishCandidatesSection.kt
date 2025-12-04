package com.caloriesresume.app.ui.analysis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.caloriesresume.app.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DishCandidatesSection(
    segmentationResult: SegmentationResult,
    imageUri: Uri,
    onCandidateSelected: (DishCandidate) -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Selecciona el plato detectado",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        segmentationResult.segments.forEach { segment ->
            SegmentCandidatesCard(
                segment = segment,
                imageUri = imageUri,
                imageSize = segmentationResult.processedImageSize,
                onCandidateSelected = onCandidateSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SegmentCandidatesCard(
    segment: SegmentationCandidate,
    imageUri: Uri,
    imageSize: ImageSize?,
    onCandidateSelected: (DishCandidate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Región ${segment.foodItemPosition}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            segment.candidates.forEach { candidate ->
                CandidateItem(
                    candidate = candidate,
                    imageUri = imageUri,
                    boundingBox = segment.boundingBox,
                    imageSize = imageSize,
                    onClick = { onCandidateSelected(candidate) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CandidateItem(
    candidate: DishCandidate,
    imageUri: Uri,
    boundingBox: BoundingBox?,
    imageSize: ImageSize?,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var thumbnailBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    
    LaunchedEffect(imageUri, boundingBox, imageSize) {
        thumbnailBitmap = withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (originalBitmap != null && boundingBox != null && imageSize != null) {
                    val scaleX = originalBitmap.width.toFloat() / imageSize.width
                    val scaleY = originalBitmap.height.toFloat() / imageSize.height
                    
                    val x = (boundingBox.x * scaleX).toInt().coerceIn(0, originalBitmap.width)
                    val y = (boundingBox.y * scaleY).toInt().coerceIn(0, originalBitmap.height)
                    val width = (boundingBox.w * scaleX).toInt().coerceIn(0, originalBitmap.width - x)
                    val height = (boundingBox.h * scaleY).toInt().coerceIn(0, originalBitmap.height - y)
                    
                    if (width > 0 && height > 0) {
                        Bitmap.createBitmap(originalBitmap, x, y, width, height)
                    } else null
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (thumbnailBitmap != null) {
                Image(
                    bitmap = thumbnailBitmap!!.asImageBitmap(),
                    contentDescription = candidate.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(imageUri)
                            .build()
                    ),
                    contentDescription = candidate.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    candidate.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Probabilidad: ${(candidate.prob * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                candidate.nutriScore?.let { nutriScore ->
                    Text(
                        "NutriScore: ${nutriScore.nutriScoreCategory} (${nutriScore.nutriScoreStandardized})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


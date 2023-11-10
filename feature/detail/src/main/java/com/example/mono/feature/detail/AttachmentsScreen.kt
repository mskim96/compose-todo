package com.example.mono.feature.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.icon.MonoIcons

@Composable
fun AttachmentsRoute(
    onBackClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttachmentsViewModel = hiltViewModel()
) {
    val selectedAttachment by viewModel.selectedAttachment.collectAsStateWithLifecycle()
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()

    AttachmentsScreen(
        taskId = viewModel.taskId,
        selectedAttachment = selectedAttachment,
        attachments = attachments,
        onBackClick = onBackClick,
        onAttachmentDelete = viewModel::deleteAttachment,
        updateSelectedAttachment = viewModel::updateSelectedAttachment,
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AttachmentsScreen(
    taskId: String,
    selectedAttachment: String,
    attachments: List<String>,
    onBackClick: (String, String) -> Unit,
    onAttachmentDelete: (String) -> Unit,
    updateSelectedAttachment: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (attachments.isNotEmpty() && selectedAttachment.isNotEmpty()) {
        val currentPageIndex = attachments.indexOf(selectedAttachment)
        val pagerState = rememberPagerState(
            initialPage = currentPageIndex.coerceAtLeast(0)
        ) {
            attachments.size
        }
        Scaffold(
            modifier = modifier,
            topBar = {
                AttachmentsTopAppBar(
                    currentPageIndex = currentPageIndex + 1,
                    maxPageCount = attachments.size,
                    onBackClick = { onBackClick(taskId, selectedAttachment) },
                    onAttachmentDelete = { onAttachmentDelete(attachments[pagerState.currentPage]) }
                )
            }
        ) { padding ->
            if (attachments.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    key = { it },
                    pageSize = PageSize.Fill,
                    modifier = Modifier
                        .padding(padding)
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = attachments[it],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if(pagerState.currentPage == it) updateSelectedAttachment(it)
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentsTopAppBar(
    currentPageIndex: Int,
    maxPageCount: Int,
    onBackClick: () -> Unit,
    onAttachmentDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTopAppBar(
        title = {
            Text(text = "$currentPageIndex of $maxPageCount")
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = MonoIcons.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onAttachmentDelete) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
            }
        }
    )
}
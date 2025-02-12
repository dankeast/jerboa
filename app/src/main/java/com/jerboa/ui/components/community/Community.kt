package com.jerboa.ui.components.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.MenuItem
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.PostViewModeDialog
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.theme.*

@Composable
fun CommunityTopSection(
    communityView: CommunityView,
    modifier: Modifier = Modifier,
    onClickFollowCommunity: (communityView: CommunityView) -> Unit,
    blurNSFW: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            communityView.community.banner?.also {
                PictrsBannerImage(
                    url = it,
                    modifier = Modifier.height(DRAWER_BANNER_SIZE),
                    blur = blurNSFW && communityView.community.nsfw,
                )
            }
            communityView.community.icon?.also {
                LargerCircularIcon(icon = it, blur = blurNSFW && communityView.community.nsfw)
            }
        }
        Column(
            modifier = Modifier.padding(MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = communityView.community.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Row {
                Text(
                    text = stringResource(
                        R.string.community_users_month,
                        communityView.counts.users_active_month,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
            Row {
                when (communityView.subscribed) {
                    SubscribedType.Subscribed -> {
                        OutlinedButton(
                            onClick = { onClickFollowCommunity(communityView) },
                        ) {
                            Text(stringResource(R.string.community_joined))
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(ACTION_BAR_ICON_SIZE),
                            )
                        }
                    }
                    SubscribedType.NotSubscribed -> {
                        Button(
                            onClick = { onClickFollowCommunity(communityView) },
                        ) {
                            Text(stringResource(R.string.community_subscribe))
                        }
                    }

                    SubscribedType.Pending -> {
                        Button(
                            onClick = { onClickFollowCommunity(communityView) },
                        ) {
                            Text(stringResource(R.string.community_pending))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CommunityTopSectionPreview() {
    CommunityTopSection(
        communityView = sampleCommunityView,
        onClickFollowCommunity = {},
        blurNSFW = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHeader(
    communityName: String,
    onClickSortType: (SortType) -> Unit,
    onBlockCommunityClick: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedSortType: SortType,
    selectedPostViewMode: PostViewMode,
    onClickCommunityInfo: () -> Unit,
    onClickBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    siteVersion: String,
    isBlocked: Boolean,
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }
    var showPostViewModeOptions by remember { mutableStateOf(false) }

    if (showSortOptions) {
        SortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
            onClickSortTopOptions = {
                showSortOptions = false
                showTopOptions = !showTopOptions
            },
            siteVersion = siteVersion,
        )
    }

    if (showTopOptions) {
        SortTopOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            },
            siteVersion = siteVersion,
        )
    }

    if (showPostViewModeOptions) {
        PostViewModeDialog(
            onDismissRequest = { showPostViewModeOptions = false },
            selectedPostViewMode = selectedPostViewMode,
            onClickPostViewMode = {
                showPostViewModeOptions = false
                onClickPostViewMode(it)
            },
        )
    }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            CommunityHeaderTitle(
                communityName = communityName,
                selectedSortType = selectedSortType,
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.community_back),
                )
            }
        },
        actions = {
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = stringResource(R.string.community_sortBy),
                )
            }
            Box {
                IconButton(onClick = {
                    showMoreOptions = !showMoreOptions
                }) {
                    Icon(
                        Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.moreOptions),
                    )
                }
                CommunityMoreDropdown(
                    expanded = showMoreOptions,
                    onDismissRequest = { showMoreOptions = false },
                    onClickRefresh = onClickRefresh,
                    onClickShowPostViewModeDialog = {
                        showMoreOptions = false
                        showPostViewModeOptions = true
                    },
                    onBlockCommunityClick = {
                        showMoreOptions = false
                        onBlockCommunityClick()
                    },
                    onClickCommunityInfo = onClickCommunityInfo,
                    isBlocked = isBlocked,
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommunityHeaderTitle(
    communityName: String,
    selectedSortType: SortType,
) {
    val ctx = LocalContext.current
    Column {
        Text(
            text = communityName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            modifier = Modifier.basicMarquee(),
        )
        Text(
            text = ctx.getString(selectedSortType.shortForm),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun CommunityMoreDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onBlockCommunityClick: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickCommunityInfo: () -> Unit,
    onClickShowPostViewModeDialog: () -> Unit,
    isBlocked: Boolean,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MenuItem(
            text = stringResource(R.string.community_refresh),
            icon = Icons.Outlined.Refresh,
            onClick = {
                onDismissRequest()
                onClickRefresh()
            },
        )
        MenuItem(
            text = stringResource(R.string.home_post_view_mode),
            icon = Icons.Outlined.ViewAgenda,
            onClick = {
                onDismissRequest()
                onClickShowPostViewModeDialog()
            },
        )
        MenuItem(
            text = stringResource(R.string.community_community_info),
            icon = Icons.Outlined.Info,
            onClick = {
                onClickCommunityInfo()
                onDismissRequest()
            },
        )
        MenuItem(
            text = stringResource(
                if (isBlocked) {
                    R.string.community_unblock_community
                } else R.string.community_block_community,
            ),
            icon = Icons.Outlined.Block,
            onClick = onBlockCommunityClick,
        )
    }
}

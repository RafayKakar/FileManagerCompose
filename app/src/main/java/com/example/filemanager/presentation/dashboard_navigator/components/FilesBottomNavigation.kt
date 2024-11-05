package com.example.filemanager.presentation.dashboard_navigator.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.filemanager.R
import com.example.filemanager.presentation.Dimens
import com.example.filemanager.presentation.Dimens.IconSize
import com.example.filemanager.presentation.dashboard_navigator.BottomNavigationItem
import com.example.filemanager.ui.theme.FileManagerTheme


@Composable
fun FilesBottomNavigation(
    items: List<BottomNavigationItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color("#C4E1F6".toColorInt()),
        tonalElevation = 15.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = { onItemClick(index) },
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            modifier = Modifier.size(IconSize),
                        )
                        Spacer(modifier = Modifier.height(Dimens.ExtraSmallPadding2))
                        Text(text = item.text, style = MaterialTheme.typography.labelSmall)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = colorResource(id = R.color.body),
                    unselectedTextColor = colorResource(id = R.color.body),
                    indicatorColor = MaterialTheme.colorScheme.background,
                ),
            )

        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NewsBottomNavigationPreview() {
    FileManagerTheme(dynamicColor = false) {
        FilesBottomNavigation(items = listOf(
            BottomNavigationItem(icon = R.drawable.recents_dashboard, text = "recents"),
            BottomNavigationItem(icon = R.drawable.documents_dashboard, text = "documents"),
            BottomNavigationItem(icon = R.drawable.home_dashboard, text = "home"),
            BottomNavigationItem(icon = R.drawable.media_dashboard, text = "media"),
            BottomNavigationItem(icon = R.drawable.others_dashboard, text = "others"),
        ), selectedItem = 0, onItemClick = {})
    }
}
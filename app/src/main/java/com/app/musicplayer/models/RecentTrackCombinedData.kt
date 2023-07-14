package com.app.musicplayer.models

import com.app.musicplayer.db.entities.RecentTrackEntity

data class RecentTrackCombinedData(val track: RecentTrackEntity, val position: Int)

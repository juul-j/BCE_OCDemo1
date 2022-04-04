package com.example.musicdao.ui.screens.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicdao.core.ipv8.MusicCommunity
import com.example.musicdao.core.repositories.model.Artist
import com.example.musicdao.core.repositories.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MyProfileScreenViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val musicCommunity: MusicCommunity,
) : ViewModel() {

    private val _profile: MutableStateFlow<Artist?> = MutableStateFlow(null)
    val profile: StateFlow<Artist?> = _profile

    fun publicKey(): String {
        return musicCommunity.publicKeyHex()
    }

    fun publishEdit(
        name: String,
        bitcoinAddress: String,
        socials: String,
        biography: String
    ): Boolean {
        return artistRepository.edit(name, bitcoinAddress, socials, biography)
    }

    init {
        viewModelScope.launch {
            _profile.value = artistRepository.getArtist(musicCommunity.publicKeyHex())
        }
    }
}

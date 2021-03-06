package de.fips.plugin.tinyaudioplayer.audio;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class PlaylistAudioPlayerTest {
	private IPlaybackListener playbackListener;
	private Playlist playlist;
	private URI location1;
	private URI location2;
	private URI location3;
	private PlaylistAudioPlayer player;
	private SingleTrackAudioPlayer internalPlayer;
	
	@Before
	public void setup() {
		// setup
		playbackListener = mock(IPlaybackListener.class);
		playlist = spy(new Playlist());
		location1 = new File("Track 01.mp3").toURI();
		playlist.add(new PlaylistItem("Track 01", location1, 220));
		location2 = new File("Track 02.mp3").toURI();
		playlist.add(new PlaylistItem("Track 02", location2, 220));
		location3 = new File("Track 03.mp3").toURI();
		playlist.add(new PlaylistItem("Track 03", location3, 220));
		player = spy(new PlaylistAudioPlayer(playlist));
		internalPlayer = mock(SingleTrackAudioPlayer.class);
		player.setPlaybackHandler(playbackListener);
		doReturn(internalPlayer).when(player).createInternalPlayer(any(URI.class), anyFloat(), anyBoolean());
	}
	
	@Test
	public void whenInternalPlayerIsNotRunning_play_shouldCreateNewInternalPlayerWithCurrentTrack() {
		// run
		player.play();
		// assert
		verify(player).createInternalPlayer(eq(location1), anyFloat(), anyBoolean());
		final InOrder inorder = inOrder(internalPlayer);
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
	}

	@Test
	public void whenInternalPlayerIsRunning_play_shouldStopRunningInternalPlayerAndCreateNewInternalPlayer() {
		// run
		player.play();
		player.play();
		// assert
		verify(player, times(2)).createInternalPlayer(eq(location1), anyFloat(), anyBoolean());
		final InOrder inorder = inOrder(internalPlayer);
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
		inorder.verify(internalPlayer).stop();
		inorder.verify(internalPlayer).removePlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
	}

	@Test
	public void whenInternalPlayerIsPaused_play_shouldContinuePlaying() {
		// run
		player.play();
		player.pause();
		doReturn(true).when(internalPlayer).isPaused();
		player.play();
		// assert
		verify(player).createInternalPlayer(eq(location1), anyFloat(), anyBoolean());
		final InOrder inorder = inOrder(internalPlayer);
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
		inorder.verify(internalPlayer).pause();
		inorder.verify(internalPlayer).play();
	}

	@Test
	public void whenInternalPlayerIsNotRunning_stop_shouldDoNothing() {
		// run
		player.stop();
		// assert
		verifyZeroInteractions(internalPlayer);
	}

	@Test
	public void whenInternalPlayerIsPaused_pause_shouldContinuePlaying() {
		// run
		player.play();
		player.pause();
		doReturn(true).when(internalPlayer).isPaused();
		player.pause();
		// assert
		verify(player).createInternalPlayer(eq(location1), anyFloat(), anyBoolean());
		final InOrder inorder = inOrder(internalPlayer);
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
		inorder.verify(internalPlayer).pause();
		inorder.verify(internalPlayer).play();
	}

	@Test
	public void whenInternalPlayerIsNotRunning_previous_shouldJustChangeToThePreviousTrack() {
		// run
		player.previous();
		// assert
		verify(playlist).previous();
	}

	@Test
	public void whenInternalPlayerIsRunning_previous_shouldStopRunningInternalPlayerAndPlayPreviousTrack() {
		// setup
		player.toggleRepeat();
		// run
		player.play();
		player.previous();
		// assert
		verify(playlist).previous();
		InOrder inorder = inOrder(player);
		inorder.verify(player).createInternalPlayer(eq(location1), anyFloat(), anyBoolean());
		inorder.verify(player).createInternalPlayer(eq(location3), anyFloat(), anyBoolean());
		inorder = inOrder(internalPlayer);
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
		inorder.verify(internalPlayer).stop();
		inorder.verify(internalPlayer).removePlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
	}

	@Test
	public void whenInternalPlayerIsNotRunning_next_shouldJustChangeToTheNextTrack() {
		// run
		player.next();
		// assert
		verify(playlist).next();
	}

	@Test
	public void whenInternalPlayerIsRunning_next_shouldStopRunningInternalPlayerAndPlayNextTrack() {
		// run
		player.play();
		player.next();
		// assert
		verify(playlist).next();
		InOrder inorder = inOrder(player);
		inorder.verify(player).createInternalPlayer(eq(location1), anyFloat(), anyBoolean());
		inorder.verify(player).createInternalPlayer(eq(location2), anyFloat(), anyBoolean());
		inorder = inOrder(internalPlayer);
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
		inorder.verify(internalPlayer).stop();
		inorder.verify(internalPlayer).removePlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).addPlaybackListener(eq(playbackListener));
		inorder.verify(internalPlayer).play();
	}

	@Test
	public void testToggleShuffle() {
		// run
		player.toggleShuffle();
		// assert
		verify(playlist).toggleShuffle();
	}

	@Test
	public void testToggleRepeat() {
		// run
		player.toggleRepeat();
		// assert
		verify(playlist).toggleRepeat();
	}
}

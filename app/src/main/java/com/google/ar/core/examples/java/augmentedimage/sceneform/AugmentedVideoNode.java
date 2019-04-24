
package com.google.ar.core.examples.java.augmentedimage.sceneform;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.augmentedimage.R;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

public class AugmentedVideoNode extends AnchorNode {

    private static final String TAG = "AugmentedImageNode";

    private AugmentedImage image;


    @Nullable
    private ModelRenderable videoRenderable;
    private MediaPlayer mediaPlayer;

    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.85f;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

    public AugmentedVideoNode(Context context, AugmentedImage image, ArSceneView arSceneView) {

        ExternalTexture texture = new ExternalTexture();
        // Create an Android MediaPlayer to capture the video on the external texture's surface.

        mediaPlayer = MediaPlayer.create(context, R.raw.vid);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);


        // Upon construction, start loading the modelFuture

            ModelRenderable.builder().setRegistryId("modelFuture")
                    .setSource(context,   R.raw.chroma_key_video)
                    .build().thenAccept(
                            renderable -> {
                                videoRenderable = renderable;
                                renderable.getMaterial().setExternalTexture("videoTexture", texture);
                                renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                            })
                    .exceptionally(
                            throwable -> {
                                Toast toast =
                                        Toast.makeText(context, "Unable to load video renderable", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                return null;
                            });





        this.image = image;

//        setAnchor(image.createAnchor(image.getCenterPose()));


        // Create the Anchor.
        Anchor anchor = image.createAnchor(image.getCenterPose());
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arSceneView.getScene());


        Node videoNode = new Node();
        videoNode.setParent(anchorNode);

        //1250.720

        float videoWidth = mediaPlayer.getVideoWidth();
        float videoHeight = mediaPlayer.getVideoHeight();
        videoNode.setLocalScale(
                new Vector3(
                        VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f));


        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            // Wait to set the renderable until the first frame of the  video becomes available.
            // This prevents the renderable from briefly appearing as a black quad before the video
            // plays.
            texture
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                videoNode.setRenderable(videoRenderable);
                                texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });
        } else {
            videoNode.setRenderable(videoRenderable);
        }


}

    public AugmentedImage getImage() {
        return image;
    }
}

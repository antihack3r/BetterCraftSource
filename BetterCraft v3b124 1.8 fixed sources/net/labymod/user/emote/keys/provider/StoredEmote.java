/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys.provider;

import java.util.Iterator;
import net.labymod.user.emote.keys.EmoteKeyFrame;
import net.labymod.user.emote.keys.EmotePose;
import net.labymod.user.emote.keys.PoseAtTime;
import net.labymod.user.emote.keys.provider.EmoteProvider;
import net.labymod.user.emote.keys.provider.KeyFrameStorage;

public class StoredEmote
extends EmoteProvider {
    private Iterator<PoseAtTime>[] iterator = new Iterator[7];

    public StoredEmote(KeyFrameStorage keyFrameStorage) {
        EmoteKeyFrame[] emoteKeyFrames = keyFrameStorage.getKeyframes();
        int id2 = 0;
        while (id2 < 7) {
            int count = 0;
            EmoteKeyFrame[] emoteKeyFrameArray = emoteKeyFrames;
            int n2 = emoteKeyFrames.length;
            int n3 = 0;
            while (n3 < n2) {
                EmoteKeyFrame emoteKeyFrame = emoteKeyFrameArray[n3];
                EmotePose[] emotePoseArray = emoteKeyFrame.getEmotePoses();
                int n4 = emotePoseArray.length;
                int n5 = 0;
                while (n5 < n4) {
                    EmotePose subPose = emotePoseArray[n5];
                    if (subPose.getBodyPart() == id2) {
                        ++count;
                    }
                    ++n5;
                }
                ++n3;
            }
            final PoseAtTime[] posesAtTime = new PoseAtTime[count];
            int index = 0;
            EmoteKeyFrame[] emoteKeyFrameArray2 = emoteKeyFrames;
            int n6 = emoteKeyFrames.length;
            int n7 = 0;
            while (n7 < n6) {
                EmoteKeyFrame keyframe = emoteKeyFrameArray2[n7];
                EmotePose[] emotePoseArray = keyframe.getEmotePoses();
                int n8 = emotePoseArray.length;
                int n9 = 0;
                while (n9 < n8) {
                    EmotePose pose = emotePoseArray[n9];
                    if (pose.getBodyPart() == id2) {
                        posesAtTime[index] = new PoseAtTime(pose, keyframe.getOffset(), true);
                        ++index;
                    }
                    ++n9;
                }
                ++n7;
            }
            final int keyFrameCount = count;
            this.iterator[id2] = new Iterator<PoseAtTime>(){
                private int currentIndex = 0;

                @Override
                public boolean hasNext() {
                    return this.currentIndex < keyFrameCount && posesAtTime[this.currentIndex] != null;
                }

                @Override
                public PoseAtTime next() {
                    return posesAtTime[this.currentIndex++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            ++id2;
        }
    }

    @Override
    public boolean hasNext(int bodyPoseId) {
        return this.iterator[bodyPoseId].hasNext();
    }

    @Override
    public PoseAtTime next(int bodyPoseId) {
        return this.iterator[bodyPoseId].next();
    }

    @Override
    public boolean isWaiting() {
        return false;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void clear() {
        i = 0;
        ** GOTO lbl8
        {
            this.next(i);
            do {
                if (this.hasNext(i)) continue block0;
                ++i;
lbl8:
                // 2 sources

            } while (i < this.iterator.length);
        }
    }

    public Iterator<PoseAtTime>[] getIterator() {
        return this.iterator;
    }
}


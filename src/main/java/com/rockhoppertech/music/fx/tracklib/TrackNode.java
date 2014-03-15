package com.rockhoppertech.music.fx.tracklib;

/*
 * #%L
 * tracklib-fx
 * %%
 * Copyright (C) 2013 - 2014 Rockhopper Technologies
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import javafx.scene.layout.Region;

import com.rockhoppertech.music.midi.js.MIDITrack;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class TrackNode extends Region {
    private MIDITrack track;

    public TrackNode(MIDITrack track, TrackPane trackPane) {
        this.track = track;
        this.setHeight(100);
        this.setWidth(200);
        this.setPrefHeight(100);
        this.setPrefWidth(200);
        this.setLayoutX(0);
        this.setLayoutY(0);
        this.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 1px;");
    }

    /**
     * @return the track
     */
    public MIDITrack getTrack() {
        return track;
    }

    /**
     * @param track the track to set
     */
    public void setTrack(MIDITrack track) {
        this.track = track;
    }

}

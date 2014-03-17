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

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockhoppertech.music.midi.js.MIDITrack;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class TrackLibModel {
    private static final Logger logger = LoggerFactory
            .getLogger(TrackLibModel.class);

    private final ObservableList<MIDITrack> trackList;
    private final ListProperty<MIDITrack> tracksProperty;

    public TrackLibModel() {
        trackList = FXCollections.observableArrayList();
        tracksProperty = new SimpleListProperty<>(trackList);
    }

    public ListProperty<MIDITrack> tracksProperty() {
        return tracksProperty;
    }

    public void add(MIDITrack track) {
        //trackList.add(track);
        tracksProperty.add(track);
    }

    public void remove(MIDITrack track) {
        //trackList.remove(track);
        tracksProperty.remove(track);
    }

    private MIDITrack selectedTrack;

    public void setSelectedMIDITrack(MIDITrack track) {
        this.selectedTrack = track;

    }

    /**
     * @return the selectedTrack
     */
    public MIDITrack getSelectedTrack() {
        return selectedTrack;
    }

    /**
     * @param selectedTrack the selectedTrack to set
     */
    public void setSelectedTrack(MIDITrack selectedTrack) {
        this.selectedTrack = selectedTrack;
    }
}

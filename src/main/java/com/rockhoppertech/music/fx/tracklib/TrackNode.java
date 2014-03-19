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

import javafx.beans.property.adapter.JavaBeanDoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockhoppertech.music.midi.js.MIDITrack;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class TrackNode extends Region {
    private static final Logger logger = LoggerFactory
            .getLogger(TrackNode.class);

    private MIDITrack track;
    Text sb;
    Text name;
    JavaBeanDoubleProperty startBeatProperty;

    public TrackNode(MIDITrack track, TrackPane trackPane) {
        this.track = track;
        this.setHeight(100);
        double w = trackPane.getBeatWidth() * track.getDuration();
        this.setWidth(w);
        this.setPrefHeight(75);
        this.setPrefWidth(w);
        this.setLayoutX(0);
        this.setLayoutY(0);
        this.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 1px;");
        this.setCursor(Cursor.HAND);

        name = new Text(this.track.getName());
        name.setFontSmoothingType(FontSmoothingType.LCD);
        double h = name.getLayoutBounds().getHeight();
        name.setLayoutX(0);
        name.setLayoutY(h);
        this.getChildren().add(name);

        sb = new Text("" + this.track.getStartBeat());
        sb.setFontSmoothingType(FontSmoothingType.LCD);
        sb.setLayoutX(0);
        sb.setLayoutY(h * 2d);
        this.getChildren().add(sb);

        try {
            startBeatProperty = JavaBeanDoublePropertyBuilder.create()
                    .bean(this.track)
                    // .bean(this.track.get(0))
                    .name("startBeat")
                    .build();
            startBeatProperty.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0,
                        Number arg1, Number arg2) {
                    logger.debug("listener new {}", arg2);
                    sb.setText("" + arg2.doubleValue());
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        leftResizeRect = new Rectangle(0, 0, 5, this.getPrefHeight());
        leftResizeRect.setCursor(Cursor.W_RESIZE);
        leftResizeRect.setFill(Color.BLUE);
        getChildren().add(leftResizeRect);

        rightResizeRect = new Rectangle(this.getPrefWidth() - 15, 0,
                15, this.getPrefHeight());

        rightResizeRect.setCursor(Cursor.E_RESIZE);
        rightResizeRect.setFill(Color.BLUE);
        getChildren().add(rightResizeRect);
        rightResizeRect.toFront();
        rightResizeRect.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                logger.debug("resize r x {}", e.getX());
                e.consume();

            }
        });
        rightResizeRect.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {

            }
        });

    }

    Rectangle rightResizeRect;
    Rectangle leftResizeRect;

    /**
     * @return the track
     */
    public MIDITrack getTrack() {
        return track;
    }

    /**
     * @param track
     *            the track to set
     */
    public void setTrack(MIDITrack track) {
        this.track = track;
    }

}

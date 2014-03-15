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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockhoppertech.music.PitchFormat;
import com.rockhoppertech.music.midi.js.MIDITrack;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class TrackLibAppController {
    private static final Logger logger = LoggerFactory
            .getLogger(TrackLibAppController.class);

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    private Stage stage;

    @FXML
    private AnchorPane root;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<MIDITrack> trackList;

    @FXML
    private TrackPane trackPane;

    @FXML
    void exitAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void doitAction(ActionEvent event) {

    }

    @FXML
    void playAction(ActionEvent event) {

        MIDITrack track = trackList.getSelectionModel().getSelectedItem();
        if (track != null) {
            track.play();
        }

    }

    @FXML
    void addAction(ActionEvent event) {
        MIDITrack track = new MIDITrack();
        track.setName("" + System.currentTimeMillis());
        model.add(track);
    }

    @FXML
    void removeAction(ActionEvent event) {
        // MIDITrack track = trackList.getSelectionModel().getSelectedItem();
        // if (track != null) {
        // model.remove(track);
        // }

        final int selectedIdx = trackList.getSelectionModel()
                .getSelectedIndex();
        if (selectedIdx != -1) {
            MIDITrack track = trackList.getSelectionModel().getSelectedItem();
            model.remove(track);
            final int newSelectedIdx =
                    (selectedIdx == trackList.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            trackList.getItems().remove(selectedIdx);
            trackList.getSelectionModel().select(newSelectedIdx);
        }
    }

    private TrackLibModel model;

    @FXML
    void initialize() {

        PitchFormat.getInstance().setWidth(4);
        model = new TrackLibModel();

        trackPane.refresh();

        setupDragonDrop();

        trackList.setCellFactory(new Callback<ListView<MIDITrack>,
                ListCell<MIDITrack>>() {
                    @Override
                    public ListCell<MIDITrack> call(ListView<MIDITrack> list) {
                        return new TrackListCell();
                    }
                });
        trackList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<MIDITrack>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends MIDITrack> observable,
                            MIDITrack oldValue, MIDITrack newValue) {

                        // model.setMIDITrack(newValue);

                        // SymParams params = model.getParamsForTrack(newValue);
                        if (newValue != null) {
//                            SymParams params = (SymParams) newValue
//                                    .getUserData();
//                            logger.debug("params {}", params);
//                            if (params != null) {
//
//                            }
                        }
                    }
                });

        model.tracksProperty().addListener(new ListChangeListener<MIDITrack>() {
            @Override
            public void onChanged(
                    javafx.collections.ListChangeListener.Change<? extends MIDITrack> change) {
                logger.debug("list change {}", change);
                while (change.next()) {
                    if (change.wasAdded()) {
                        trackList.getItems().addAll(change.getAddedSubList());
                        for (MIDITrack track : change.getAddedSubList()) {
                            trackPane.add(track);
                        }
                    } else if (change.wasRemoved()) {
                        logger.debug("removing {}", change.getRemoved());
                        trackList.getItems().remove(change.getRemoved());
                    } else if (change.wasReplaced()) {
                    }
                }
            }
        });

    }

    /**
     * Make the track readable as a String.
     * 
     * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
     * 
     */
    static class TrackListCell extends ListCell<MIDITrack> {
        @Override
        public void updateItem(MIDITrack item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                this.setText(item.getName() + "-" + System.currentTimeMillis());
            }
        }
    }

    protected void setupDragonDrop() {

        trackList.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /*
                 * data is dragged over the target; accept it only if it is not
                 * dragged from the same node and if it has MIDITrack data
                 */
                if (event.getGestureSource() != trackList &&
                        event.getDragboard().hasContent(midiTrackDataFormat)) {
                    logger.debug("drag over");
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                // Don't consume the event. Let the layers below process the
                // DragOver event as well so that the
                // translucent container image will follow the cursor.
                // event.consume();
            }
        });

        trackList.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                /* show to the user that it is an actual gesture target */
                logger.debug("drag entered");
                if (event.getGestureSource() != trackList &&
                        event.getDragboard().hasContent(midiTrackDataFormat)) {
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(5.0);
                    dropShadow.setOffsetX(3.0);
                    dropShadow.setOffsetY(3.0);
                    dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
                    trackList.setEffect(dropShadow);
                }
                event.consume();
            }
        });

        trackList.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                trackList.setEffect(null);
                event.consume();
            }
        });

        trackList.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(midiTrackDataFormat)) {
                    MIDITrack track = (MIDITrack) db
                            .getContent(midiTrackDataFormat);
                    trackList.getItems().add(track);
                    success = true;

                }
                /*
                 * let the source know whether the data was successfully
                 * transferred and used
                 */
                event.setDropCompleted(success);
                event.consume();
            }
        });

    }

    /**
     * For Dragon drop.
     * <p>
     * The string HAS to be in this format, otherwise on OSX, you'll get
     * "Cannot set data for an invalid UTI"
     */
    private static final DataFormat midiTrackDataFormat = new DataFormat(
            "com.rockhoppertech.music.midi.js.MIDITrack");

}

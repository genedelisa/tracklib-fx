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

import com.rockhoppertech.music.Pitch;
import com.rockhoppertech.music.PitchFactory;
import com.rockhoppertech.music.PitchFormat;
import com.rockhoppertech.music.fx.cmn.musicxml.TrackToMusicXML;
import com.rockhoppertech.music.fx.components.RTIDialog;
import com.rockhoppertech.music.midi.gm.MIDIGMPatch;
import com.rockhoppertech.music.midi.js.Instrument;
import com.rockhoppertech.music.midi.js.MIDINote;
import com.rockhoppertech.music.midi.js.MIDITrack;
import com.rockhoppertech.music.midi.js.MIDITrackBuilder;
import javafx.beans.property.Property;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private TableColumn<MIDINote, Double> columnDuration;

    @FXML
    private TableColumn<MIDINote, Pitch> columnPitch;

    @FXML
    private TableColumn<MIDINote, Double> columnStart;

    @FXML
    private TableColumn<MIDINote, Integer> columnChannel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextArea rawTextArea;

    @FXML
    private TextField nameTextField;

    @FXML
    private ComboBox<Instrument> instrumentComboBox;

    @FXML
    // private TableView<MIDITrack> trackTable;
    private TableView<MIDINote> trackTable;
    private ObservableList<MIDINote> tableDataList;

    private File currentSaveFile;

    @FXML
    void exportTracktoMusicXML(ActionEvent event) {
        MIDITrack track = trackList.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save selected track to MusicXML File");
        // if (currentSaveFile == null) {
        currentSaveFile = fileChooser.showSaveDialog(this.stage);
        if (!currentSaveFile.getName().endsWith(".xml")) {
            currentSaveFile = new File(currentSaveFile.getAbsolutePath()
                    + ".xml");
        }
        logger.debug("exporting to {}", currentSaveFile);
        // }
        String workTitle = "Stuff";
        String composer = "Mozart";
        if (currentSaveFile != null) {
            try {
                FileOutputStream out = new FileOutputStream(currentSaveFile);
                TrackToMusicXML.emitXML(track, out, workTitle, composer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                RTIDialog.showMessageDialog(e.getLocalizedMessage());
                e.printStackTrace();
            } catch (XMLStreamException e) {
                RTIDialog.showMessageDialog(e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
    }

    @FXML
    void exitAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void saveChangesAction(ActionEvent event) {
        MIDITrack track = trackList.getSelectionModel().getSelectedItem();
        track.setName(nameTextField.getText());
        track.setDescription(descriptionTextArea.getText());

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
        MIDITrack track = MIDITrackBuilder.create()
                .name("" + System.currentTimeMillis())
                .noteString("C")
                .build();

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

    ObservableList<String> pitchList;
    ObservableList<Integer> midiValues;
    ObservableList<Integer> channelList;

    @FXML
    void initialize() {

        PitchFormat.getInstance().setWidth(4);
        model = new TrackLibModel();

        trackPane.refresh();

        configureInstrumentComboBox();

        setupDragonDrop();

        this.descriptionTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                MIDITrack track = trackList.getSelectionModel().getSelectedItem();
                track.setDescription(newValue);
            }
        });
        this.nameTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                MIDITrack track = trackList.getSelectionModel().getSelectedItem();
                track.setName(newValue);
            }
        });



        // columnStart
        // .setCellValueFactory(new PropertyValueFactory<MIDINote, Double>(
        // "startBeat"));

        configureTrackTableView();
        this.tableDataList = FXCollections.observableArrayList();

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

                        // SymParams params = model.getParamsForTrack(newValue);
                        if (newValue != null) {
                            logger.debug("handling list item select");
                            model.setSelectedMIDITrack(newValue);
                            tableDataList.clear();
                            for (MIDINote note : newValue) {
                                tableDataList.add(note);
                            }
                            trackTable.setItems(tableDataList);

                            nameTextField.setText(newValue.getName());
                            descriptionTextArea.setText(newValue
                                    .getDescription());
                            rawTextArea.setText(newValue.toString());

                            instrumentComboBox.getSelectionModel().select(
                                    newValue.getInstrument());

                            // SymParams params = (SymParams) newValue
                            // .getUserData();
                            // logger.debug("params {}", params);
                            // if (params != null) {
                            //
                            // }
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
                            logger.debug("added to trackpane {}", track);
                            
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
                    // trackList.getItems().add(track);
                    model.add(track);
                    logger.debug("track dropped {}", track);
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

    void configureTrackTableView() {
        channelList = FXCollections
                .observableArrayList();
        for (int i = 0; i < 16; i++) {
            channelList.add(i);
        }

        midiValues = FXCollections.observableArrayList();
        for (int i = 0; i < 128; i++) {
            midiValues.add(i);
        }
        pitchList = FXCollections.observableArrayList();
        for (int i = 0; i < 128; i++) {
            pitchList.add(PitchFormat
                    .midiNumberToString(i));
        }

        StringConverter<Double> dsc = new StringConverter<Double>() {
            @Override
            public String toString(Double t) {
                return String.format("%f", t);
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        };

        // StringConverter<MIDINote> midiNoteStartConverter = new
        // StringConverter<MIDINote>() {
        // @Override
        // public String toString(MIDINote t) {
        // return String.format("%f", t.getStartBeat());
        // }
        //
        // @Override
        // public MIDINote fromString(String string) {
        // throw new UnsupportedOperationException("Not supported yet.");
        // }
        // };
        StringConverter<Pitch> stringPitchConverter = new StringConverter<Pitch>() {
            @Override
            public String toString(Pitch t) {
                return PitchFormat.getInstance().format(t);
            }

            @Override
            public Pitch fromString(String string) {
                return PitchFactory.getPitch(string);
            }
        };

        columnStart
                .setCellValueFactory(new Callback<CellDataFeatures<MIDINote, Double>, ObservableValue<Double>>() {
                    public ObservableValue<Double> call(
                            CellDataFeatures<MIDINote, Double> note) {
                        // note.getValue() returns the MIDINote instance for a
                        // particular TableView row
                        MIDINote n = note.getValue();
                        Property p = null;
                        try {
                            p = JavaBeanDoublePropertyBuilder
                                    .create()
                                    .bean(n)
                                    .name("startBeat")
                                    .build();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        return p;

                        // return note.getValue().firstNameProperty();
                    }
                });
        columnStart.setCellFactory(TextFieldTableCell
                .<MIDINote, Double> forTableColumn(dsc));
        columnStart.setOnEditCommit(
                new EventHandler<CellEditEvent<MIDINote, Double>>() {
                    @Override
                    public void handle(CellEditEvent<MIDINote, Double> t) {
                        (t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setStartBeat(t.getNewValue());
                    }
                }
                );

        columnChannel
                .setCellValueFactory(new Callback<CellDataFeatures<MIDINote, Integer>, ObservableValue<Integer>>() {
                    public ObservableValue<Integer> call(
                            CellDataFeatures<MIDINote, Integer> note) {
                        MIDINote n = note.getValue();
                        Property p = null;
                        try {
                            p = JavaBeanIntegerPropertyBuilder
                                    .create()
                                    .bean(n)
                                    .name("channel")
                                    .build();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        return p;
                    }
                });
        columnChannel.setOnEditCommit(
                new EventHandler<CellEditEvent<MIDINote, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<MIDINote, Integer> t) {
                        ( t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setChannel(t.getNewValue());
                    }
                }
                );
        columnChannel.setCellFactory(
                ComboBoxTableCell.<MIDINote, Integer> forTableColumn(
                        channelList));

        columnDuration
                .setCellValueFactory(new Callback<CellDataFeatures<MIDINote, Double>, ObservableValue<Double>>() {
                    public ObservableValue<Double> call(
                            CellDataFeatures<MIDINote, Double> note) {
                        MIDINote n = note.getValue();
                        Property p = null;
                        try {
                            p = JavaBeanDoublePropertyBuilder
                                    .create()
                                    .bean(n)
                                    .name("duration")
                                    .build();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        return p;
                    }
                });
        columnDuration.setCellFactory(TextFieldTableCell
                .<MIDINote, Double> forTableColumn(dsc));
        columnDuration.setOnEditCommit(
                new EventHandler<CellEditEvent<MIDINote, Double>>() {
                    @Override
                    public void handle(CellEditEvent<MIDINote, Double> t) {
                        ( t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setDuration(t.getNewValue());
                    }
                }
                );

        columnPitch.setCellFactory(TextFieldTableCell
                .<MIDINote, Pitch> forTableColumn(stringPitchConverter));
        columnPitch.setOnEditCommit(
                new EventHandler<CellEditEvent<MIDINote, Pitch>>() {
                    @Override
                    public void handle(CellEditEvent<MIDINote, Pitch> t) {
                        ( t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setPitch(t.getNewValue());
                    }
                }
                );
        columnPitch
                .setCellValueFactory(new Callback<CellDataFeatures<MIDINote, Pitch>, ObservableValue<Pitch>>() {
                    public ObservableValue<Pitch> call(
                            CellDataFeatures<MIDINote, Pitch> note) {
                        MIDINote n = note.getValue();
                        Property p = null;
                        try {
                            p = JavaBeanObjectPropertyBuilder
                                    .create()
                                    .bean(n)
                                    .name("pitch")
                                    .build();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        return p;
                    }
                });
    }

    static class ProgramStringListCell extends
            ComboBoxTableCell<MIDINote, String> {

        static ObservableList<String> patchNames;
        static {
            patchNames = FXCollections.observableArrayList();
            for (MIDIGMPatch p : MIDIGMPatch.getAllPitched()) {
                patchNames.add(p.getName());
            }
        }

        public ProgramStringListCell() {
            super(patchNames);
        }

        public ProgramStringListCell(ObservableList<String> programStrings) {
            super(programStrings);
        }

        @Override
        public void updateItem(String patchName, boolean empty) {
            super.updateItem(patchName, empty);
            if (patchName != null) {
                setText(MIDIGMPatch.getPatch(patchName).getName());
                logger.debug("patch name {}", patchName);

                if (getTableRow() != null) {
                    MIDINote note = (MIDINote) getTableRow().getItem();
                    if (note != null) {
                        logger.debug("Table row {}", note);
                        int program = MIDIGMPatch.getPatch(patchName)
                                .getProgram();
                        note.setProgram(program);
                    }
                }
            }
        }
    }

    void configureInstrumentComboBox() {

        ObservableList<Instrument> instrumentList = FXCollections
                .observableArrayList();
        for (Instrument p : Instrument.getAll()) {
            logger.debug("adding instrument {}", p);
            instrumentList.add(p);
        }
        instrumentComboBox.setItems(instrumentList);

        instrumentComboBox.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<Instrument>() {
                    public void changed(
                            ObservableValue<? extends Instrument> source,
                            Instrument oldValue, Instrument newValue) {
                        logger.debug("You selected: " + newValue);
                        model.getSelectedTrack().setInstrument(newValue);
                    }
                });

        instrumentComboBox.setCellFactory(
                new Callback<ListView<Instrument>, ListCell<Instrument>>() {
                    @Override
                    public ListCell<Instrument> call(
                            ListView<Instrument> param) {

                        final ListCell<Instrument> cell = new ListCell<Instrument>() {
                            @Override
                            public void updateItem(Instrument item,
                                    boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item.getName());
                                }
                                else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                });

        // otherwise, you'd get the Instrument toString when combo is in unselected state
        instrumentComboBox.setConverter(new StringConverter<Instrument>() {
            @Override
            public String toString(Instrument inst) {
                if (inst == null) {
                    return null;
                } else {
                    return inst.getName();
                }
            }

            @Override
            public Instrument fromString(String name) {
                return Instrument.getByName(name);
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

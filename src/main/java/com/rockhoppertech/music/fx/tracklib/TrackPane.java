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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rockhoppertech.music.midi.js.MIDITrack;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class TrackPane extends Pane {
    final static Logger logger = LoggerFactory.getLogger(TrackPane.class);

    private MultipleSelectionModel<TrackPane> selectionModel;
    private ObservableList<MIDITrack> tracks;
    private int beatWidth = 80;
    private int preferredWidth = this.beatWidth * 10;
    private int snapX;
    private int snapY;
    private boolean snapOn = true;
    private int snapBeatDivision = 4;
    private double currentEndBeat = 32d;
    private List<Shape> beatLines = new ArrayList<>();
    private Map<MIDITrack, TrackNode> trackComponentMap = new HashMap<MIDITrack, TrackNode>();

    public TrackPane() {
        getStyleClass().setAll("trackpane-control");

        selectionModel = new MultipleSelectionModel<TrackPane>() {

            @Override
            public ObservableList<Integer> getSelectedIndices() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ObservableList<TrackPane> getSelectedItems() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void selectAll() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void selectFirst() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void selectIndices(int arg0, int... arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void selectLast() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void clearAndSelect(int arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void clearSelection() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void clearSelection(int arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public boolean isEmpty() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isSelected(int arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void select(int arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void select(TrackPane arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void selectNext() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void selectPrevious() {
                // TODO Auto-generated method stub
                
            }
           
        };
        

        // nah, use the model's tracks
        this.tracks = FXCollections.observableArrayList();
        this.snapX = this.beatWidth / this.snapBeatDivision;
        this.snapY = 1;
        this.setMinWidth(100d);
        this.setMinHeight(100d);
        this.setWidth(1000d);
        this.setHeight(300d);
        createBeats();
        drawLines();
        this.setStyle("-fx-background-color: antiquewhite; -fx-border-color: black; -fx-border-width: 1px;");
        // dragModeActiveProperty.bind(dragModeCheckbox.selectedProperty());
    }

    public void refresh() {
        createBeats();
        drawLines();
    }

    public void drawLines() {
        this.getChildren().addAll(this.beatLines);
    }

    private void createBeats() {
        beatLines.clear();
        double x = beatWidth;
        double height = this.getBoundsInLocal().getHeight();
        double width = this.getBoundsInLocal().getWidth();
        logger.debug("width {} height {}", width, height);
        logger.debug("pwidth {} ", getBoundsInParent().getWidth());
        for (x = beatWidth; x < width; x += beatWidth) {
            Line line = new Line(x, 0, x, height);
            // line.setFill(Color.BLACK);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2d);
            beatLines.add(line);
            logger.debug("line {}", line);
        }
    }

    public TrackPane clear() {
        this.getChildren().clear();
        return this;
    }

    public TrackPane add(final TrackNode node) {
        this.getChildren().add(makeDraggable(node));
        return this;
    }

    public void add(final MIDITrack track) {
        this.tracks.add(track);
        // notelist.addPropertyChangeListener(this);
        final TrackNode trackNode = new TrackNode(track, this);

        this.add(trackNode);

        trackComponentMap.put(track,
                trackNode);

        trackNode.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
        // tc.onMouseMovedProperty().

        EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("Handling event " + event.getEventType());

                event.consume();
            }
        };
        // tc.addEventHandler(MouseEvent.MOUSE_DRAGGED, handler);

        // trackNode.setOnMouseEntered(handler);
        // trackNode.setOnMouseExited(handler);
        // trackNode.setOnMousePressed(handler);
        // trackNode.setOnMouseDragged(handler);
        // trackNode.setOnMouseReleased(handler);

        //
        // tc.addComponentListener(this);
        // tc.addPropertyChangeListener(this);
        // tc.addMouseListener(this.dmh);
        // tc.addMouseMotionListener(this.dmh);
        if (track.getEndBeat() > this.currentEndBeat) {
            this.currentEndBeat = track.getEndBeat();
        }
    }

    public double getBeatForX(double x) {
        return x / this.beatWidth + 1d;
    }

    private final BooleanProperty dragModeActiveProperty =
            new SimpleBooleanProperty(this, "dragModeActive", true);

    private Node makeDraggable(final Node node) {
        final DragContext dragContext = new DragContext();
        final Group wrapGroup = new Group(node);

        wrapGroup.addEventFilter(
                MouseEvent.ANY,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            // disable mouse events for all children
                            mouseEvent.consume();
                        }
                    }
                });

        wrapGroup.addEventFilter(
                MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            // remember initial mouse cursor coordinates
                            // and node position
                            dragContext.mouseAnchorX = mouseEvent.getX();
                            dragContext.mouseAnchorY = mouseEvent.getY();
                            dragContext.initialTranslateX =
                                    node.getTranslateX();
                            dragContext.initialTranslateY =
                                    node.getTranslateY();
                        }
                    }
                });

        wrapGroup.addEventFilter(
                MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            // shift node from its initial position by delta
                            // calculated from mouse cursor movement
                            double xx = dragContext.initialTranslateX
                                    + mouseEvent.getX()
                                    - dragContext.mouseAnchorX;
                            System.err.println("xx " + xx);
                            // xx = xx - xx % 25;
                            // xx -= xx % 25;
                            xx -= xx % snapX;
                            if (xx < 0) {
                                xx = 0;
                            }
                            System.err.println("sx " + xx);
                            node.setTranslateX(xx);

                            node.setTranslateY(
                                    dragContext.initialTranslateY
                                            + mouseEvent.getY()
                                            - dragContext.mouseAnchorY);
                            logger.debug("dragged to beat {}", getBeatForX(xx));
                        }
                    }
                });

        wrapGroup.addEventFilter(
                MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        if (dragModeActiveProperty.get()) {
                            double xx = dragContext.initialTranslateX
                                    + mouseEvent.getX()
                                    - dragContext.mouseAnchorX;
                            // xx -= xx % 25;
                            xx -= xx % snapX;
                            if (xx < 0) {
                                xx = 0;
                            }
                            node.setTranslateX(xx);
                            node.setTranslateY(
                                    dragContext.initialTranslateY
                                            + mouseEvent.getY()
                                            - dragContext.mouseAnchorY);
                            logger.debug("released at beat {}", getBeatForX(xx));
                            if (node instanceof TrackNode) {
                                TrackNode tn = (TrackNode) node;
                                tn.getTrack().setStartBeat(getBeatForX(xx));
                            }

                        }
                    }
                });

        return wrapGroup;
    }

    private static final class DragContext {
        public double mouseAnchorX;
        public double mouseAnchorY;
        public double initialTranslateX;
        public double initialTranslateY;
    }
}

package com.rockhoppertech.music.fx.tracklib.db;

import com.rockhoppertech.music.fx.cmn.musicxml.TrackToMusicXML;
import com.rockhoppertech.music.midi.js.MIDITrack;

import javax.persistence.*;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by gene on 3/21/14.
 */

@Entity

@NamedQueries({
        @NamedQuery(name = TrackEntity.ALL, query = "SELECT t FROM TrackEntity t "),
        @NamedQuery(name = TrackEntity.TOTAL, query = "SELECT COUNT(t) FROM TrackEntity t")}
)
public class TrackEntity {
    public final static String ALL = "TrackEntity.allTracks";
    public final static String TOTAL = "TrackEntity.count";


    private Integer id;

    private String musicXML;

    private String name;

    private String description;

    private MIDITrack track;

    @Basic
    @Column(nullable = true)
    public String getName() {
        if(this.track == null) {
            return null;
        }
        return this.track.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(nullable = true)
    public String getDescription() {
        if(this.track == null) {
            return null;
        }
        return this.track.getDescription();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Lob
    @Column(nullable = false)
    public String getMusicXML() {

        if(this.track == null) {
            System.err.println("track is null!");
            return null;
        }
        StringWriter sw = new StringWriter();
        try {
            TrackToMusicXML.emitXML(this.track, sw,"title","moats art" );
            musicXML =  sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return musicXML;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMusicXML(String musicXML) {
        this.musicXML = musicXML;
    }

    @Transient
    public MIDITrack getTrack() {
        return track;
    }

    public void setTrack(MIDITrack track) {
        this.track = track;
        System.out.println("set track to " + track);
    }

}

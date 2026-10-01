package com.flymaccin.tuneflow;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int BACKGROUND = Color.rgb(13, 15, 20);
    private static final int SURFACE = Color.rgb(23, 26, 34);
    private static final int SURFACE_LIGHT = Color.rgb(34, 38, 49);
    private static final int TEXT = Color.rgb(244, 245, 248);
    private static final int MUTED = Color.rgb(154, 160, 174);
    private static final int ACCENT = Color.rgb(111, 132, 255);

    private final ArrayList<TrackModel> tracks = new ArrayList<>();
    private SongModel song;
    private TrackModel selectedTrack;
    private LinearLayout page;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BACKGROUND);
        getWindow().setNavigationBarColor(BACKGROUND);
        createDemoSong();
        render();
    }

    private void createDemoSong() {
        song = new SongModel("Midnight Study", 120, "4/4", "A minor");
        tracks.add(new TrackModel("Keys", "MIDI", "Warm piano", 0, 8, Color.rgb(111, 132, 255), true,
                notes(new int[][]{{60, 0, 2}, {64, 2, 2}, {67, 4, 3}, {64, 8, 2}, {62, 10, 2}, {60, 12, 4}})));
        tracks.add(new TrackModel("Bass", "MIDI", "Sub bass", 0, 8, Color.rgb(56, 190, 160), true,
                notes(new int[][]{{36, 0, 4}, {43, 4, 4}, {41, 8, 4}, {38, 12, 4}})));
        tracks.add(new TrackModel("Drums", "MIDI", "Beat pattern", 0, 8, Color.rgb(239, 166, 76), true,
                notes(new int[][]{{36, 0, 1}, {42, 2, 1}, {38, 4, 1}, {42, 6, 1}, {36, 8, 1}, {42, 10, 1}, {38, 12, 1}, {42, 14, 1}})));
        tracks.add(new TrackModel("Vocal", "AUDIO", "Verse take", 2, 5, Color.rgb(220, 103, 139), false,
                new ArrayList<MidiNote>()));
        selectedTrack = tracks.get(0);
    }

    private ArrayList<MidiNote> notes(int[][] values) {
        ArrayList<MidiNote> result = new ArrayList<>();
        for (int[] value : values) {
            result.add(new MidiNote(value[0], value[1], value[2]));
        }
        return result;
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BACKGROUND);
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(18), dp(20), dp(30));
        scroll.addView(page);

        TextView eyebrow = text("TUNEFLOW  /  MUSIC WORKSPACE", 11, MUTED, true);
        page.addView(eyebrow);
        TextView title = text("Song workspace", 27, TEXT, true);
        LinearLayout.LayoutParams titleParams = params(-2, -2);
        titleParams.topMargin = dp(6);
        page.addView(title, titleParams);
        TextView subtitle = text("Explore how a song is organized.", 14, MUTED, false);
        page.addView(subtitle, params(-2, -2));

        addSongCard();
        addSectionTitle("ARRANGEMENT", "A song contains tracks, and tracks contain clips.");
        addTrackControls();
        for (TrackModel track : tracks) {
            addTrackCard(track);
        }
        addMidiEditor();

        setContentView(scroll);
    }

    private void addSongCard() {
        LinearLayout card = card();
        LinearLayout.LayoutParams cardParams = params(-1, -2);
        cardParams.topMargin = dp(20);
        page.addView(card, cardParams);
        TextView name = text(song.name, 20, TEXT, true);
        card.addView(name);
        TextView description = text("SONG  ·  Demo project", 11, MUTED, true);
        LinearLayout.LayoutParams descParams = params(-2, -2);
        descParams.topMargin = dp(5);
        card.addView(description, descParams);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams statsParams = params(-1, -2);
        statsParams.topMargin = dp(18);
        card.addView(stats, statsParams);
        addSongStat(stats, "TEMPO", song.tempo + " BPM");
        addSongStat(stats, "METER", song.meter);
        addSongStat(stats, "KEY", song.key);
    }

    private void addSongStat(LinearLayout row, String label, String value) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(item, new LinearLayout.LayoutParams(0, dp(44), 1));
        item.addView(text(label, 10, MUTED, true));
        TextView stat = text(value, 15, TEXT, true);
        LinearLayout.LayoutParams valueParams = params(-2, -2);
        valueParams.topMargin = dp(4);
        item.addView(stat, valueParams);
    }

    private void addSectionTitle(String heading, String detail) {
        TextView label = text(heading, 11, ACCENT, true);
        LinearLayout.LayoutParams labelParams = params(-2, -2);
        labelParams.topMargin = dp(24);
        page.addView(label, labelParams);
        TextView hint = text(detail, 13, MUTED, false);
        LinearLayout.LayoutParams hintParams = params(-1, -2);
        hintParams.topMargin = dp(4);
        page.addView(hint, hintParams);
    }

    private void addTrackControls() {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowParams = params(-1, -2);
        rowParams.topMargin = dp(14);
        page.addView(row, rowParams);
        TextView count = text(tracks.size() + " tracks", 13, MUTED, true);
        row.addView(count, new LinearLayout.LayoutParams(0, -2, 1));
        Button add = button("+  Add MIDI track");
        row.addView(add, params(-2, 42));
        add.setOnClickListener(view -> {
            int number = tracks.size() + 1;
            TrackModel track = new TrackModel("New track " + number, "MIDI", "MIDI clip", 0, 4,
                    ACCENT, true, new ArrayList<MidiNote>());
            tracks.add(track);
            selectedTrack = track;
            render();
        });
    }

    private void addTrackCard(TrackModel track) {
        boolean selected = track == selectedTrack;
        LinearLayout card = card();
        card.setBackground(background(selected ? SURFACE_LIGHT : SURFACE, selected ? ACCENT : SURFACE, 14));
        LinearLayout.LayoutParams cardParams = params(-1, -2);
        cardParams.topMargin = dp(10);
        page.addView(card, cardParams);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(header, params(-1, -2));
        View dot = new View(this);
        dot.setBackground(background(track.color, track.color, 50));
        header.addView(dot, new LinearLayout.LayoutParams(dp(9), dp(9)));
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams labelsParams = new LinearLayout.LayoutParams(0, -2, 1);
        labelsParams.leftMargin = dp(10);
        header.addView(labels, labelsParams);
        labels.addView(text(track.name, 15, TEXT, true));
        TextView type = text(track.kind + "  ·  " + track.clip.name, 11, MUTED, false);
        LinearLayout.LayoutParams typeParams = params(-2, -2);
        typeParams.topMargin = dp(3);
        labels.addView(type, typeParams);

        Button mute = button(track.muted ? "MUTED" : "MUTE");
        mute.setTextSize(10);
        header.addView(mute, params(-2, 36));
        mute.setOnClickListener(view -> {
            track.muted = !track.muted;
            render();
        });
        card.setOnClickListener(view -> {
            selectedTrack = track;
            render();
        });

        TimelineView timeline = new TimelineView(track);
        LinearLayout.LayoutParams timelineParams = params(-1, 42);
        timelineParams.topMargin = dp(13);
        card.addView(timeline, timelineParams);
        timeline.setOnClickListener(view -> {
            selectedTrack = track;
            render();
        });
    }

    private void addMidiEditor() {
        addSectionTitle("CLIP EDITOR", "MIDI clips hold notes; audio clips hold recorded audio.");
        LinearLayout card = card();
        LinearLayout.LayoutParams cardParams = params(-1, -2);
        cardParams.topMargin = dp(12);
        page.addView(card, cardParams);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(header, params(-1, -2));
        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        header.addView(labels, new LinearLayout.LayoutParams(0, -2, 1));
        labels.addView(text(selectedTrack.clip.name, 16, TEXT, true));
        TextView detail = text(selectedTrack.name + "  ·  " + selectedTrack.clip.kind + " CLIP", 11, MUTED, true);
        LinearLayout.LayoutParams detailParams = params(-2, -2);
        detailParams.topMargin = dp(4);
        labels.addView(detail, detailParams);

        TextView noteCount = text(selectedTrack.clip.notes.size() + " notes", 12, ACCENT, true);
        if (selectedTrack.clip.isMidi) {
            header.addView(noteCount);
        }

        if (selectedTrack.clip.isMidi) {
            PianoRollView pianoRoll = new PianoRollView(selectedTrack.clip.notes);
            LinearLayout.LayoutParams rollParams = params(-1, 190);
            rollParams.topMargin = dp(16);
            card.addView(pianoRoll, rollParams);
            Button addNote = button("+  Add MIDI note");
            LinearLayout.LayoutParams buttonParams = params(-1, 44);
            buttonParams.topMargin = dp(12);
            card.addView(addNote, buttonParams);
            addNote.setOnClickListener(view -> {
                int step = selectedTrack.clip.notes.size() % 16;
                int pitch = midiEditorBasePitch() + (selectedTrack.clip.notes.size() % 12);
                selectedTrack.clip.notes.add(new MidiNote(pitch, step, 2));
                render();
            });
        } else {
            TextView audioInfo = text("Audio clip  ·  Bar " + (selectedTrack.clip.startBar + 1)
                    + "  ·  " + selectedTrack.clip.barLength + " bars", 14, MUTED, false);
            LinearLayout.LayoutParams audioParams = params(-1, 94);
            audioParams.topMargin = dp(16);
            audioInfo.setGravity(Gravity.CENTER);
            audioInfo.setBackground(background(SURFACE_LIGHT, SURFACE_LIGHT, 10));
            card.addView(audioInfo, audioParams);
        }
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(14), dp(14), dp(14), dp(14));
        layout.setBackground(background(SURFACE, SURFACE, 14));
        return layout;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(TEXT);
        button.setTextSize(12);
        button.setAllCaps(false);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(12), 0, dp(12), 0);
        button.setBackground(background(SURFACE_LIGHT, SURFACE_LIGHT, 10));
        return button;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        if (bold) {
            text.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return text;
    }

    private GradientDrawable background(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        if (fill != stroke) {
            drawable.setStroke(dp(1), stroke);
        }
        return drawable;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width < 0 ? width : dp(width), height < 0 ? height : dp(height));
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int midiEditorBasePitch() {
        int lowestPitch = 60;
        if (!selectedTrack.clip.notes.isEmpty()) {
            lowestPitch = 127;
            for (MidiNote note : selectedTrack.clip.notes) {
                lowestPitch = Math.min(lowestPitch, note.pitch);
            }
        }
        return Math.min(115, lowestPitch / 12 * 12);
    }

    private class TimelineView extends View {
        private final TrackModel track;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TimelineView(TrackModel track) {
            super(MainActivity.this);
            this.track = track;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth();
            float height = getHeight();
            float labelWidth = dp(24);
            float timelineWidth = width - labelWidth;
            paint.setColor(SURFACE_LIGHT);
            canvas.drawRoundRect(new RectF(labelWidth, 0, width, height), dp(7), dp(7), paint);
            paint.setColor(Color.rgb(68, 73, 87));
            paint.setStrokeWidth(dp(1));
            for (int bar = 0; bar <= 8; bar++) {
                float x = labelWidth + timelineWidth * bar / 8f;
                canvas.drawLine(x, 0, x, height, paint);
            }
            float left = labelWidth + timelineWidth * track.clip.startBar / 8f + dp(2);
            float right = labelWidth + timelineWidth * (track.clip.startBar + track.clip.barLength) / 8f - dp(2);
            paint.setColor(track.color);
            canvas.drawRoundRect(new RectF(left, dp(5), right, height - dp(5)), dp(6), dp(6), paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(dp(10));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            canvas.drawText(track.clip.name, left + dp(8), height / 2f + dp(4), paint);
            paint.setColor(MUTED);
            paint.setTextSize(dp(9));
            canvas.drawText("1", labelWidth - dp(12), height / 2f + dp(4), paint);
        }
    }

    private class PianoRollView extends View {
        private final ArrayList<MidiNote> notes;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int basePitch;
        private final String[] pitchNames = {"C", "C#", "D", "D#", "E", "F",
                "F#", "G", "G#", "A", "A#", "B"};

        PianoRollView(ArrayList<MidiNote> notes) {
            super(MainActivity.this);
            this.notes = notes;
            int lowestPitch = 60;
            if (!notes.isEmpty()) {
                lowestPitch = 127;
                for (MidiNote note : notes) {
                    lowestPitch = Math.min(lowestPitch, note.pitch);
                }
            }
            basePitch = Math.min(115, lowestPitch / 12 * 12);
            setBackground(background(SURFACE_LIGHT, SURFACE_LIGHT, 10));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float labelWidth = dp(32);
            float top = dp(4);
            float rowHeight = (getHeight() - top * 2) / 12f;
            float gridWidth = getWidth() - labelWidth - dp(4);
            float stepWidth = gridWidth / 16f;
            paint.setTextSize(dp(9));
            paint.setColor(MUTED);
            for (int row = 0; row < 12; row++) {
                float y = top + row * rowHeight;
                int pitch = basePitch + 11 - row;
                if (row == 0 || row == 4 || row == 7) {
                    paint.setColor(Color.rgb(45, 49, 61));
                    canvas.drawRect(labelWidth, y, getWidth(), y + rowHeight, paint);
                    paint.setColor(MUTED);
                }
                String pitchLabel = pitchNames[pitch % 12] + (pitch / 12 - 1);
                canvas.drawText(pitchLabel, dp(5), y + rowHeight * 0.72f, paint);
                paint.setColor(row % 12 == 0 ? Color.rgb(81, 86, 101) : Color.rgb(54, 59, 71));
                canvas.drawLine(labelWidth, y, getWidth(), y, paint);
                paint.setColor(MUTED);
            }
            for (int step = 0; step <= 16; step++) {
                float x = labelWidth + step * stepWidth;
                paint.setColor(step % 8 == 0 ? Color.rgb(105, 111, 132) : Color.rgb(54, 59, 71));
                canvas.drawLine(x, top, x, getHeight() - top, paint);
            }
            for (MidiNote note : notes) {
                if (note.pitch < basePitch || note.pitch > basePitch + 11) {
                    continue;
                }
                int row = basePitch + 11 - note.pitch;
                float left = labelWidth + note.startStep * stepWidth + dp(1);
                float right = left + Math.max(dp(8), note.duration * stepWidth - dp(2));
                float noteTop = top + row * rowHeight + dp(2);
                paint.setColor(ACCENT);
                canvas.drawRoundRect(new RectF(left, noteTop, Math.min(right, getWidth() - dp(2)),
                        noteTop + rowHeight - dp(4)), dp(4), dp(4), paint);
            }
        }
    }

    private static class SongModel {
        final String name;
        final int tempo;
        final String meter;
        final String key;

        SongModel(String name, int tempo, String meter, String key) {
            this.name = name;
            this.tempo = tempo;
            this.meter = meter;
            this.key = key;
        }
    }

    private static class TrackModel {
        final String name;
        final String kind;
        final int color;
        final boolean midi;
        final ClipModel clip;
        boolean muted;

        TrackModel(String name, String kind, String clipName, int startBar, int barLength,
                   int color, boolean midi, ArrayList<MidiNote> notes) {
            this.name = name;
            this.kind = kind;
            this.color = color;
            this.midi = midi;
            this.clip = new ClipModel(clipName, kind, startBar, barLength, midi, notes);
        }
    }

    private static class ClipModel {
        final String name;
        final String kind;
        final int startBar;
        final int barLength;
        final boolean isMidi;
        final ArrayList<MidiNote> notes;

        ClipModel(String name, String kind, int startBar, int barLength, boolean isMidi, ArrayList<MidiNote> notes) {
            this.name = name;
            this.kind = kind;
            this.startBar = startBar;
            this.barLength = barLength;
            this.isMidi = isMidi;
            this.notes = notes;
        }
    }

    private static class MidiNote {
        final int pitch;
        final int startStep;
        final int duration;

        MidiNote(int pitch, int startStep, int duration) {
            this.pitch = pitch;
            this.startStep = startStep;
            this.duration = duration;
        }
    }
}

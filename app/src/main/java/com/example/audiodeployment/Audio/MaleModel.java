package com.example.audiodeployment.Audio;

import android.media.AudioRecord;
import android.os.Bundle;
import android.view.View;

import com.example.audiodeployment.helpers.MaleModelActivity;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MaleModel extends MaleModelActivity {

    String model = "Female_model.tflite";
    float probabilityThreshold = 0.3f;
    AudioClassifier classifier;
    private TensorAudio tensor;
    private AudioRecord record;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classifier = AudioClassifier.createFromFile(this,model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create tensor recorder
        tensor = classifier.createInputTensorAudio();
    }

    @Override
    public void onStartRecording(View view) {
        super.onStartRecording(view);

        // showing the audio recorder specification
        TensorAudio.TensorAudioFormat format = classifier.getRequiredTensorAudioFormat();
        String specs = "Number of channels: " + format.getChannels() + "\n"
                + "Sample Rate: " + format.getSampleRate();
        specsTextView.setText(specs);

        // Creating and start recording
        record = classifier.createAudioRecord();
        record.startRecording();

        timerTask = new TimerTask() {
            @Override
            public void run() {

                List<Classifications> output = classifier.classify(tensor);

                // Filtering out classifications with low probability

                List<Category> finalOutput = new ArrayList<>();
                for (Classifications classifications : output) {
                    for (Category category : classifications.getCategories()) {
                        if (category.getScore() > 0.3f) {
                            finalOutput.add(category);
                        }
                    }
                }

                // Sorting the results
                //Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

                // Creating a multiline string with the filtered results
                StringBuilder outputStr = new StringBuilder();
                for (Category category : finalOutput) {
                    outputStr.append(category.getLabel())
                            .append(": ").append(category.getScore()).append("\n");
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (finalOutput.isEmpty()) {
                            outputTextView.setText("Could not classify");
                        } else {
                            outputTextView.setText(outputStr.toString());
                        }

                    }
                });


            }
        };

        new Timer().scheduleAtFixedRate(timerTask, 1, 500);

    }

    @Override
    public void onStopRecording(View view) {
        super.onStopRecording(view);

        timerTask.cancel();
        record.stop();

    }
}

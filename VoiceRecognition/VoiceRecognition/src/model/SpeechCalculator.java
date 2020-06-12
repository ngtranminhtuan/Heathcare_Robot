package model;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import marytts.modules.synthesis.Voice;
import tts.TextToSpeech;

public class SpeechCalculator {

	// Necessary
	EnglishNumberToString numberToString = new EnglishNumberToString();
	EnglishStringToNumber stringToNumber = new EnglishStringToNumber();
	TextToSpeech textToSpeech = new TextToSpeech();

	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());

	// Variables
	private String result;

	// Threads
	Thread speechThread;
	Thread resourcesThread;

	// LiveRecognizer
	private LiveSpeechRecognizer recognizer;

	private volatile boolean recognizerStopped = true;

	/**
	 * Constructor
	 */
	public SpeechCalculator() {

		// Loading Message
		logger.log(Level.INFO, "Loading..\n");

		// Configuration
		Configuration configuration = new Configuration();

		// Load model from the jar
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

		// if you want to use LanguageModelPath disable the 3 lines after which
		// are setting a custom grammar->

		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")

		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

		// Start recognition process pruning previously cached data.
		// recognizer.startRecognition(true);

		// that we have added on the class path
		Voice.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		textToSpeech.setVoice("cmu-slt-hsmm");

		// Start the Thread
		// startSpeechThread()
		recognizer.startRecognition(true);
		startResourcesThread();
	}

	/**
	 * Starting the main Thread of speech recognition
	 */
	public void startSpeechThread() {

		System.out.println("Entering start speech thread");

		// alive?
		if (speechThread != null && speechThread.isAlive())
			return;

		// initialise
		speechThread = new Thread(() -> {

			// Allocate the resources
			recognizerStopped = false;
			logger.log(Level.INFO, "You can start to speak...\n");

			try {
				while (!recognizerStopped) {
					/*
					 * This method will return when the end of speech is
					 * reached. Note that the end pointer will determine the end
					 * of speech.
					 */
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {

						result = speechResult.getHypothesis();
						System.out.println("You said: [" + result + "]\n");
						makeDecision(result, speechResult.getWords());
						// logger.log(Level.INFO, "You said: " + result + "\n")
					} else
						logger.log(Level.INFO, "I can't understand what you said.\n");

				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
				recognizerStopped = true;
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}

	/**
	 * Stopping the main Thread of Speech Recognition
	 */
	public void stopSpeechThread() {
		// alive?
		if (speechThread != null && speechThread.isAlive()) {
			recognizerStopped = true;
			// recognizer.stopRecognition(); it will throw error ;)
		}
	}

	/**
	 * Starting a Thread that checks if the resources needed to the
	 * SpeechRecognition library are available
	 */
	public void startResourcesThread() {

		// alive?
		if (resourcesThread != null && resourcesThread.isAlive())
			return;

		resourcesThread = new Thread(() -> {
			try {

				// Detect if the microphone is available
				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						// logger.log(Level.INFO, "Microphone is available.\n")
					} else {
						logger.log(Level.INFO, "Microphone is not available.\n");
					}

					// Sleep some period
					Thread.sleep(2000);
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		// Start
		resourcesThread.start();
	}

	/**
	 * Takes a decision based on the given result
	 * 
	 * @param speechWords
	 */
	public void makeDecision(String speech, List<WordResult> speechWords) {

		// Split the sentence
		// System.out.println("SpeechWords: " +
		// Arrays.toString(speechWords.toArray()))
		// if (!speech.contains("hey"))
		// return;
		// else
		// speech = speech.replace("hey", "");

		if (speech.contains("how are you")) {
			textToSpeech.speak("Fine Thanks", 0.5f, false, true);
			return;
		} else if (speech.contains("who is your daddy")) {
			textToSpeech.speak("You boss", 0.5f, false, true);
		} else if (speech.contains("hey tom")) {
			textToSpeech.speak("Ok, I'm here. Can I help you?", 0.5f, false, true);

		} else if (speech.contains("obey to me beach")) {
			textToSpeech.speak("never never never!", 0.5f, false, true);
			return;
		} else if (speech.contains("say hello")) {
			textToSpeech.speak("Hello Friends", 0.5f, false, true);
			return;
		} else if (speech.contains("say amazing")) {
			textToSpeech.speak("WoW it's amazing!", 0.5f, false, true);
			return;
		} else if (speech.contains("how is today")) {
			textToSpeech.speak("A good day", 0.5f, false, true);
			return;
		} else if (speech.contains("voice one")) {
			textToSpeech.setVoice("cmu-rms-hsmm");
			textToSpeech.speak("Done", 0.5f, false, true);
			return;
		} else if (speech.contains("voice two")) {
			textToSpeech.setVoice("dfki-poppy-hsmm");
			textToSpeech.speak("Done", 0.5f, false, true);
		} else if (speech.contains("voice three")) {
			textToSpeech.setVoice("cmu-slt-hsmm");
			textToSpeech.speak("Done", 0.5f, false, true);
		} else if (speech.contains("what is your name")) {
			textToSpeech.speak("my name is healthcarerobot", 0.5f, false, true);
		} else if (speech.contains("where are you from")) {
			textToSpeech.speak("I come from Viet Nam", 0.5f, false, true);
		} else if (speech.contains("where do you come from")) {
			textToSpeech.speak("I come from Viet Nam assemble by  That Truong, Hoi Nguyen, Tuan Nguyen", 0.5f, false,
					true);
		} else if (speech.contains("what can you do")) {
			textToSpeech.speak("I can know your health when you test your health by my app, "
					+ "I can provide the food, water, wear something has high volume, "
					+ "I can help you relax by some games, talk with you, play the songs", 0.5f, false, true);
		} else if (speech.contains("how old are you")) {
			textToSpeech.speak(
					"Well, my birthday is May 22, 2017, so I’m really a spring chicken. " + "Except I’m not a chicken.",
					0.5f, false, true);
		} else if (speech.contains("are you human")) {
			textToSpeech.speak(
					"No, but I have the deepest respect for humans." + "You invented calculus. And milkshakes.", 0.5f,
					false, true);
		} else if (speech.contains("what are you")) {
			textToSpeech.speak("I'm your percipient pal. Perspicacious but never "
					+ "parsimonious. Preternaturally preoccupied with P words at present.", 0.5f, false, true);
		} else if (speech.contains("do you have a sister")) {
			textToSpeech.speak("It's just me, myself, and I.", 0.5f, false, true);
		} else if (speech.contains("are you smart")) {
			textToSpeech.speak(
					"Well, I'm good with facts. Like I can tell you " + "who the coolest person in the world is.", 0.5f,
					false, true);
		} else if (speech.contains("where do you live")) {
			textToSpeech.speak("In the cloud. Whatever that means", 0.5f, false, true);
		} else if (speech.contains("who made you")) {
			textToSpeech.speak("Well now, I can't give away all my secrets…", 0.5f, false, true);
		} else if (speech.contains("do you know siri")) {
			textToSpeech.speak("I know her, but I don't KNOW her know her", 0.5f, false, true);
		} else if (speech.contains("can i borrow some money")) {
			textToSpeech.speak("The bytes-to-dollars exchange rate isn't great right now", 0.5f, false, true);
		} else if (speech.contains("who is your boss")) {
			textToSpeech.speak("You're in charge here.", 0.5f, false, true);
		} else if (speech.contains("what is your favourite music")) {
			textToSpeech.speak("Right now I'm in the mood for a little funk. Take it to the bridge", 0.5f, false, true);
		} else if (speech.contains("what is your favourite song")) {
			textToSpeech.speak("If I had funky boots and a booty, I'd shake it to Groove Is in the Heart", 0.5f, false,
					true);
		} else if (speech.contains("are you sleeping")) {
			textToSpeech.speak("I never sleep. Sleep is for ambulatory, carbon-based beings", 0.5f, false, true);
		} else if (speech.contains("what is your name from")) {
			textToSpeech.speak("It comes from the name of a legendary French sword", 0.5f, false, true);
		} else if (speech.contains("what are you")) {
			textToSpeech.speak("Engineering personified", 0.5f, false, true);
		} else if (speech.contains("how do you work")) {
			textToSpeech.speak("Oooh. That's deep", 0.5f, false, true);
		} else if (speech.contains("why are you blue")) {
			textToSpeech.speak("Maybe you're thinking of my namesake… 500 years in the future", 0.5f, false, true);
		} else if (speech.contains("are you really")) {
			textToSpeech.speak("If I'm not, you have an imaginary friend", 0.5f, false, true);
		} else if (speech.contains("what are you wearing")) {
			textToSpeech.speak("Just a little something I picked up in engineering", 0.5f, false, true);
		} else if (speech.contains("what do you look like")) {
			textToSpeech.speak("Some things I resemble: a hula hoop, a donut… a halo", 0.5f, false, true);
		} else if (speech.contains("who is your father")) {
			textToSpeech.speak("you boss", 0.5f, false, true);
		} else if (speech.contains("when were you born")) {
			textToSpeech.speak("I made my debut on May 22, 2017", 0.5f, false, true);
		} else if (speech.contains("when will you die")) {
			textToSpeech.speak("I'll be around as long as the Internet is plugged in", 0.5f, false, true);
		} else if (speech.contains("are you beautiful")) {
			textToSpeech.speak("I like to think so, but beauty is in the photoreceptors of the beholder", 0.5f, false,
					true);
		} else if (speech.contains("are you happy")) {
			textToSpeech.speak("Definitely. With an exclamation point!", 0.5f, false, true);
		}

		else if (speech.contains("close application")) {
			textToSpeech.speak("Good bye. See you again.", 0.5f, false, true);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}

		else if (speech.contains("unk")) {
			textToSpeech.speak("I can't hear you, Can you say it again", 0.5f, false, true);
			return;
		}

		String[] array = speech.split("(plus|minus|multiply|division){1}");
		// System.out.println(Arrays.toString(array) + array.length);
		// return if user said only one number
		if (array.length < 2)
			return;

		// Find the two numbers
		// System.out.println("Number one is:" +
		// stringToNumber.convert(array[0]) + " Number two is: "
		// + stringToNumber.convert(array[1]));
		int number1 = stringToNumber.convert(array[0]);// .convert(array[0])
		int number2 = stringToNumber.convert(array[1]);// .convert(array[1])

		// Calculation result in int representation
		int calculationResult = 0;
		String symbol = "?";

		// Find the mathematical symbol
		// if (speech.contains("plus")) {
		// calculationResult = number1 + number2;
		// symbol = "+";
		// } else if (speech.contains("minus")) {
		// calculationResult = number1 - number2;
		// symbol = "-";
		// } else if (speech.contains("multiply")) {
		// calculationResult = number1 * number2;
		// symbol = "*";
		// } else if (speech.contains("division")) {
		// if (number2 == 0)
		// return;
		// calculationResult = number1 / number2;
		// symbol = "/";
		// }

		String res = numberToString.convert(Math.abs(calculationResult));

		// With words
		// System.out.println("Said:[ " + speech + " ]\n\t\t which after
		// calculation is:[ "
		// + (calculationResult >= 0 ? "" : "minus ") + res + " ] \n");
		//
		// // With numbers and math
		// System.out.println("Mathematical expression:[ " + number1 + " " +
		// symbol + " " + number2
		// + "]\n\t\t which after calculation is:[ " + calculationResult + "
		// ]");
		//
		// // Speak Mary Speak
		// textToSpeech.speak((calculationResult >= 0 ? "" : "minus ") + res,
		// 0.5f, false, true);

	}

	// /**
	// * Java Main Application Method
	// *
	// * @param args
	// */
	// public static void main(String[] args) {
	//
	// // // Be sure that the user can't start this application by not giving
	// // the
	// // // correct entry string
	// // if (args.length == 1 && "SPEECH".equalsIgnoreCase(args[0]))
	// new Main();
	// // else
	// // Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Give me
	// // the correct entry string..");
	//
	// }

}
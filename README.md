# MOBG6 Project

This repository contains the sources of the KnowItAll project.

## Description

KnowItAll is the ultimate quiz app designed to test and expand your general knowledge.
You will be presented with a new quiz, factual or random information that will help you learn something new and fascinating.
Whether it's animals, history, art, science or culture, you'll never run out of things to discover.

## Data persistence

A list of the last connections as well as the player's experience is stored locally in the device.

## Backend

The backend of the KnowItAll app is responsible for handling the interaction with the OpenAI API and processing the generated quiz questions and their correct answers.

## Service rest

To retrieve the content to read (from wikipedia's api), KnowItAll utilizes Retrofit, a type-safe REST client for Android. Retrofit aims to simplify the consumption of RESTful web services, making it easier and more efficient.

# CS50 Project - Pokedex

Name: Daryl Wong<br/> 
Final Project Title: Pokédex<br/>
Programming Language: Java

This project is an Android application that simulates a Pokédex from the video game, Pokémon. In the game, users collect 
different creatures known as Pokémon and a Pokédex is a tool that lets players look up different Pokémon and keep track of 
which ones they have caught.

Upon launching the app, the user is presented with a list of the original 151 Pokémon. When one Pokémon is selected from the 
list, the user is taken to a page that displays more information about that particular Pokémon including their name, photo, 
number, type, and a short description. This information is accessed via public RESTful APIs on https://pokeapi.co/.

On each individual Pokémon's page, there is a CTA for a user to select "Catch" to indicate if they have caught a Pokémon. If 
"Catch" is selected, the CTA will change to "Release", and vice versa. The state of the CTA will persist even when the user 
closes the app and returns at a later time.


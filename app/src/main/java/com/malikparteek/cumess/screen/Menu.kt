package com.malikparteek.cumess.screen

data class Menu(
    var breakfast: List<String>? = emptyList(),
    var lunch: List<String>? = emptyList(),
    var snacks: List<String>? = emptyList(),
    var snacks_girls: List<String>? = emptyList(),
    var dinner: List<String>? = emptyList(),
    var dinner_south: List<String>? = emptyList()


    )

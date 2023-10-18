package com.mobdeve.s12.aiwear

import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class UserGenerator {
    companion object {

        fun generateUsers() : ArrayList<User> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val users = ArrayList<User>()
            users.add( User (
                "SorkGa81uHgoJ1OHpon7k4wQr1l1",
                "Kim Williame Lee",
                "MOBDEVE so fun!",
                "Female",
                dateFormat.parse("2002-02-05")
            ))
            users.add( User (
                "SorkGa81uHgoJ1OHpon7k4wQr1l0",
                "Rory Gilmore",
                "stars hollow sumn",
                "Female",
                dateFormat.parse("1999-10-05")
            ))
            users.add( User (
                "SorkGa91uHgoJ1OHpon7k4wQr1l0",
                "Katniss Everdeen",
                "i volunteer!",
                "Non-Binary",
                dateFormat.parse("1989-12-05")
            ))


            return users
        }
    }

}
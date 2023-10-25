package com.mobdeve.s12.aiwear

import java.text.SimpleDateFormat
import java.util.TimeZone

class DataHelper {
    companion object {

        fun generateUsers() : ArrayList<UserModel> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val users = ArrayList<UserModel>()
            users.add( UserModel (
                "SorkGa81uHgoJ1OHpon7k4wQr1l1",
                "Kim Williame Lee",
                "MOBDEVE so fun!",
                "Female",
                dateFormat.parse("2002-02-05")
            ))
            users.add( UserModel (
                "SorkGa81uHgoJ1OHpon7k4wQr1l0",
                "Rory Gilmore",
                "stars hollow sumn",
                "Female",
                dateFormat.parse("1999-10-05")
            ))
            users.add( UserModel (
                "SorkGa91uHgoJ1OHpon7k4wQr1l0",
                "Katniss Everdeen",
                "i volunteer!",
                "Non-Binary",
                dateFormat.parse("1989-12-05")
            ))


            return users
        }

        fun generateNotifications() : ArrayList<NotificationModel> {
            var notifications = ArrayList<NotificationModel>()
            val deviceTimezone = TimeZone.getDefault()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            notifications.add( NotificationModel(
                "default",
                "This is the FIRST Notification 🌟",
                "Trying this notification system eme eme bla bla bla lorem ipsum yass",
                dateFormat.parse("2023-10-24 14:30:00"),
                false
            ))

            notifications.add( NotificationModel(
                "default",
                "Are YOUR READY?",
                "I'me ready! I'm ready! I'm ready!",
                dateFormat.parse("2023-02-05 07:36:23"),
                false
            ))

            notifications.add( NotificationModel(
                "default",
                "123",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam in scelerisque sem. Mauris " +
                        "volutpat, dolor id interdum ullamcorper, risus dolor egestas lectus, sit amet mattis purus " +
                        "dui nec risus. Maecenas non sodales nisi, vel dictum dolor. ",
                dateFormat.parse("2023-10-24 14:30:00"),
                true
            ))

            return notifications
        }
    }

}
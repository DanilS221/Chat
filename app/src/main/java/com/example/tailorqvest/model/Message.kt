package com.example.tailorqvest.model

class Message {
    var messageId: String? = null
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var imageUrl: String? = null
    var timeStamp: Long = 0

    constructor() {}

    constructor(message: String?, senderId: String?,  receiverId: String?, timeStamp: Long) {
        this.message = message
        this.senderId = senderId
        this.receiverId = receiverId
        this.timeStamp = timeStamp
    }


    fun getTextMessage(): String? {
        return message
    }
}
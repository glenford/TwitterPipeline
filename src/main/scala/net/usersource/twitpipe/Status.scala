package net.usersource.twitpipe

import akka.serialization._
import akka.serialization.Serializable.ScalaJSON
import akka.serialization.JsonSerialization._
import akka.serialization.DefaultProtocol._
import reflect.BeanInfo
import annotation.target.field
import sjson.json.JSONTypeHint


object JSON {

  implicit lazy val UserFormat: sjson.json.Format[User] =
    asProduct9("id_str",
      "screen_name",
      "name",
      "lang",
      "location",
      "time_zone",
      "description",
      "statuses_count",
      "url")(User)(User.unapply(_).get)

  @BeanInfo
  case class User(
                   id_str: String,
                   screen_name: String,
                   name: String,
                   lang: String,
                   location: Option[String],
                   time_zone: String,
                   description: Option[String],
                   statuses_count: Long,
                   url: Option[String]
                  ) extends ScalaJSON[User] {


    def toJSON: String = JsValue.toJson(tojson(this))
    def toBytes: Array[Byte] = tobinary(this)
    def fromBytes(bytes: Array[Byte]) = frombinary[User](bytes)
    def fromJSON(js: String) = fromjson[User](Js(js))

  }

  implicit lazy val GeoFormat: sjson.json.Format[Geo] =
    asProduct2("latitude", "longitude")(Geo)(Geo.unapply(_).get)

  @BeanInfo
  case class Geo(
                  latitude: Double,
                  longitude: Double
                  ) extends ScalaJSON[Geo] {


    def toJSON: String = JsValue.toJson(tojson(this))
    def toBytes: Array[Byte] = tobinary(this)
    def fromBytes(bytes: Array[Byte]) = frombinary[Geo](bytes)
    def fromJSON(js: String) = fromjson[Geo](Js(js))
  }

  implicit lazy val StatusFormat: sjson.json.Format[Status] =
    asProduct7(
      "id_str",
      "text",
      "created_at",
      "user",
      "geo",
      "in_reply_to_screen_name",
      "in_reply_to_status_id_str"
      )(Status)(Status.unapply(_).get)


  @BeanInfo
  case class Status(
                     id_str: String,
                     text: String,
                     created_at: String,
                     @(JSONTypeHint @field)(value = classOf[User])user: User,
                     @(JSONTypeHint @field)(value = classOf[Option[Geo]])geo: Option[Geo],
                     in_reply_to_screen_name: Option[String],
                     in_reply_to_status_id_str: Option[String]
                    ) extends ScalaJSON[Status] {


    def toJSON: String = JsValue.toJson(tojson(this))
    def toBytes: Array[Byte] = tobinary(this)
    def fromBytes(bytes: Array[Byte]) = frombinary[Status](bytes)
    def fromJSON(js: String) = fromjson[Status](Js(js))

  }

}
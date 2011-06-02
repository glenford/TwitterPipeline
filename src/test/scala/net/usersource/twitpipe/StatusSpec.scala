package net.usersource.twitpipe

import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen, FeatureSpec}
import org.scalatest.mock.MockitoSugar
import akka.testkit.TestKit

import net.usersource.twitpipe.JSON._

import sjson.json.JsonSerialization._
import dispatch.json.Js

class StatusSpec extends FeatureSpec with GivenWhenThen with MustMatchers with BeforeAndAfterEach with MockitoSugar with TestKit {

    feature("Parsing a status") {

      scenario("able to parse a Geo") {
        given("some geo")
        val geo = Js("""{"latitude":0.000,"longitude":90.000}""")

        when("parsed")
        val parsed = fromjson[Geo](geo)

        then("elements must match")
        parsed.latitude must be === 0.0
        parsed.longitude must be === 90.0
      }


      scenario("able to parse a valid status") {
        given("a valid status")
        val status = Js("""{"text":"i need to buy a new camera","geo":null,"truncated":false,"coordinates":null,"in_reply_to_user_id":null,"source":"web","retweet_count":0,"in_reply_to_status_id":null,"created_at":"Thu Jun 02 18:59:36 +0000 2011","favorited":false,"in_reply_to_status_id_str":null,"entities":{"hashtags":[],"urls":[],"user_mentions":[]},"place":null,"in_reply_to_screen_name":null,"in_reply_to_user_id_str":null,"id_str":"76362345886138368","user":{"profile_background_tile":true,"contributors_enabled":false,"statuses_count":8633,"followers_count":573,"profile_image_url":"http:\/\/a3.twimg.com\/profile_images\/1366154254\/get-attachment.aspx53_normal.jpg","is_translator":false,"favourites_count":0,"profile_link_color":"737373","location":"OHIO","listed_count":42,"profile_sidebar_border_color":"1d1e21","description":"fuck your thoughts! :D \r\nhttp:\/\/foreverinkiii.tumblr.com\/page\/4 #followme \r\nhttp:\/\/twitpic.com\/photos\/lasciviousCHARR","screen_name":"lasciviousCHARR","time_zone":"Central Time (US & Canada)","verified":false,"notifications":null,"profile_use_background_image":true,"created_at":"Tue Jan 04 03:14:17 +0000 2011","friends_count":410,"profile_background_color":"000000","default_profile_image":false,"lang":"en","profile_background_image_url":"http:\/\/a3.twimg.com\/profile_background_images\/253559760\/ol_dude.jpg","protected":false,"name":"Chardonee S.","id_str":"233786894","show_all_inline_media":false,"geo_enabled":false,"profile_text_color":"030203","id":233786894,"default_profile":false,"follow_request_sent":null,"following":null,"utc_offset":-21600,"profile_sidebar_fill_color":"252429","url":null},"id":76362345886138368,"contributors":null,"retweeted":false}""")
        //val status = Js("""{"text":"i need to buy a new camera","geo":null,"source":"web","created_at":"Thu Jun 02 18:59:36 +0000 2011","in_reply_to_status_id_str":null,"entities":{"hashtags":[],"urls":[],"user_mentions":[]},"place":null,"in_reply_to_screen_name":null,"in_reply_to_user_id_str":null,"id_str":"76362345886138368","user":{"profile_background_tile":true,"contributors_enabled":false,"statuses_count":8633,"followers_count":573,"profile_image_url":"http:\/\/a3.twimg.com\/profile_images\/1366154254\/get-attachment.aspx53_normal.jpg","is_translator":false,"favourites_count":0,"profile_link_color":"737373","location":"OHIO","listed_count":42,"profile_sidebar_border_color":"1d1e21","description":"fuck your thoughts! :D \r\nhttp:\/\/foreverinkiii.tumblr.com\/page\/4 #followme \r\nhttp:\/\/twitpic.com\/photos\/lasciviousCHARR","screen_name":"lasciviousCHARR","time_zone":"Central Time (US & Canada)","verified":false,"notifications":null,"profile_use_background_image":true,"created_at":"Tue Jan 04 03:14:17 +0000 2011","friends_count":410,"profile_background_color":"000000","default_profile_image":false,"lang":"en","profile_background_image_url":"http:\/\/a3.twimg.com\/profile_background_images\/253559760\/ol_dude.jpg","protected":false,"name":"Chardonee S.","id_str":"233786894","show_all_inline_media":false,"geo_enabled":false,"profile_text_color":"030203","id":233786894,"default_profile":false,"follow_request_sent":null,"following":null,"utc_offset":-21600,"profile_sidebar_fill_color":"252429","url":null}}""")

        when("parsed")
        val parsed: Status = fromjson[Status](status)

        then("object elements match the status")
        parsed.text must be === "i need to buy a new camera"
      }
      
    }
  
}
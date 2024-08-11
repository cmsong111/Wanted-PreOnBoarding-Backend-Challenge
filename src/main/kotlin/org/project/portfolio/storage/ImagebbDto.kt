package org.project.portfolio.storage

import com.fasterxml.jackson.annotation.JsonProperty

data class ImagebbDto(

    @JsonProperty("data") var data: Data? = Data(),
    @JsonProperty("success") var success: Boolean? = null,
    @JsonProperty("status") var status: Int? = null

)

data class Image(

    @JsonProperty("filename") var filename: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("mime") var mime: String? = null,
    @JsonProperty("extension") var extension: String? = null,
    @JsonProperty("url") var url: String? = null

)

data class Thumb(

    @JsonProperty("filename") var filename: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("mime") var mime: String? = null,
    @JsonProperty("extension") var extension: String? = null,
    @JsonProperty("url") var url: String? = null

)

data class Medium(

    @JsonProperty("filename") var filename: String? = null,
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("mime") var mime: String? = null,
    @JsonProperty("extension") var extension: String? = null,
    @JsonProperty("url") var url: String? = null

)

data class Data(

    @JsonProperty("id") var id: String? = null,
    @JsonProperty("title") var title: String? = null,
    @JsonProperty("url_viewer") var urlViewer: String? = null,
    @JsonProperty("url") var url: String? = null,
    @JsonProperty("display_url") var displayUrl: String? = null,
    @JsonProperty("width") var width: Int? = null,
    @JsonProperty("height") var height: Int? = null,
    @JsonProperty("size") var size: Int? = null,
    @JsonProperty("time") var time: Int? = null,
    @JsonProperty("expiration") var expiration: Int? = null,
    @JsonProperty("image") var image: Image? = Image(),
    @JsonProperty("thumb") var thumb: Thumb? = Thumb(),
    @JsonProperty("medium") var medium: Medium? = Medium(),
    @JsonProperty("delete_url") var deleteUrl: String? = null

)

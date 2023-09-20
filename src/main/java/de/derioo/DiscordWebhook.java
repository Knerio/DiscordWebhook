/*
 * MIT License
 *
 * Copyright (c) 2023 https://github.com/knerio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.derioo;

import de.derioo.objects.jsonObjects.JsonArray;
import de.derioo.objects.jsonObjects.JsonElement;
import de.derioo.objects.jsonObjects.JsonObject;
import de.derioo.objects.jsonObjects.JsonSimple;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebhook {

    private final String url;
    private final JsonObject content;


    public DiscordWebhook(String url) {
        this.url = url;
        this.content = new JsonObject();
    }

    public void setUserName(String userName) {
        this.content.addProperty("username", userName);
    }

    public void setAvatarUrl(String avatarUrl) {
        this.content.addProperty("avatar_url", avatarUrl);
    }

    public void setContent(String content) {
        this.content.addProperty("content", content);
    }

    public void addEmbedObjects(EmbedObject... objects) {

        if (this.content.contains("embeds")) {
            for (EmbedObject object : objects) {
                this.content.get("embeds").getAsJsonArray().add(object.toJsonObject());
            }
            return;
        }

        JsonArray array = new JsonArray();
        for (EmbedObject object : objects) {
            array.add(object.toJsonObject());
        }
        this.content.add("embeds", array);
    }

    public void execute() throws IOException {
        if (this.url == null) {
            throw new IllegalArgumentException("Url is empty");
        }

        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Derio");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(this.content.toString().getBytes());
        System.out.println(this.content.toString());
        stream.flush();
        stream.close();

        connection.getInputStream().close();
        connection.disconnect();
    }


    @Data
    public static class EmbedObject {

        private String title;
        private String description;
        private String url;
        private Color color;

        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private final List<Field> fields = new ArrayList<>();


        public JsonObject toJsonObject() {
            JsonObject object = new JsonObject();

            if (this.author != null) {
                object.add("author", this.author.toJsonObject());
            }
            if (this.author != null) {
                object.add("author", this.author.toJsonObject());
            }
            if (this.title != null) {
                object.addProperty("title", this.title);
            }
            if (this.url != null) {
                object.addProperty("url", this.url);
            }
            if (this.description != null) {
                object.addProperty("description", this.description);
            }


            JsonArray fields = new JsonArray();
            for (Field field : this.fields) {
                fields.add(field.toJsonObject());
            }

            if (this.color != null) {
                object.add("color", new JsonSimple(this.color.getIntColor()));
            }

            if (this.thumbnail != null) {
                object.add("thumbnail", this.thumbnail.toJsonObject());
            }

            if (this.image != null) {
                object.add("image", this.image.toJsonObject());
            }

            if (this.footer != null) {
                object.add("footer", this.footer.toJsonObject());
            }

            object.add("fields", fields);


            return object;
        }

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }
    }

    public record Footer(String text, String iconUrl) {

        public @NotNull JsonElement toJsonObject() {
            JsonObject o = new JsonObject();
            o.addProperty("text", text);
            o.addProperty("icon_url", iconUrl);

            return o;
        }
    }

    public record Thumbnail(String url) {

        public @NotNull JsonElement toJsonObject() {
            JsonObject object = new JsonObject();

            object.addProperty("url", this.url);

            return object;
        }
    }

    public record Image(String url) {

        public @NotNull JsonElement toJsonObject() {
            JsonObject object = new JsonObject();

            object.addProperty("url", this.url);

            return object;
        }
    }

    public record Author(String name, String url, String iconUrl) {

        public @NotNull JsonElement toJsonObject() {
            JsonObject object = new JsonObject();

            object.addProperty("url", this.url);
            object.addProperty("name", this.name);
            object.addProperty("icon_url", this.iconUrl);

            return object;
        }
    }

    public record Field(String name, String value, boolean inline) {

        public @NotNull JsonObject toJsonObject() {
            JsonObject object = new JsonObject();

            object.addProperty("name", name);
            object.addProperty("value", value);
            object.add("inline", new JsonSimple(inline));

            return object;
        }
    }

    public record Color(java.awt.Color color) {

        public int getIntColor() {
            int rgb = color.getRed();
            rgb = (rgb << 8) + color.getGreen();
            rgb = (rgb << 8) + color.getBlue();

            return rgb;
        }
    }
}

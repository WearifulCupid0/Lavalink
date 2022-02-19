package lavalink.server.player.track.sponsorblock;

import org.json.JSONArray;
import org.json.JSONObject;

public class Segment {
    private final String uuid;
    private final String category;
    private final String description;
    private final String actionType;
    private final long startTime;
    private final long endTime;

    private final int locked;
    private final int votes;

    public Segment(JSONObject json) {
        this.uuid = json.getString("uuid");
        this.category = json.getString("category");
        this.description = json.getString("description");
        this.actionType = json.getString("actionType");

        JSONArray times = json.getJSONArray("segment");
        this.startTime = (long) (times.getFloat(0) * 1000);
        this.endTime = (long) (times.getFloat(1) * 1000);
        
        this.locked = json.getInt("locked");
        this.votes = json.getInt("votes");
    }

    public JSONObject encode() {
        String description = this.getDescription();
        String actionType = this.getActionType();

        return new JSONObject()
        .put("uuid", this.uuid)
        .put("category", this.category)
        .put("description", description != null ? description : JSONObject.NULL)
        .put("actionType", actionType != null ? actionType : JSONObject.NULL)
        .put("startTime", this.startTime)
        .put("endTime", this.endTime)
        .put("locked", this.locked)
        .put("votes", this.votes);
    }

    public String getUUID() {
        return this.uuid;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description != null && !this.description.isBlank() ? this.description : null;
    }

    public String getActionType() {
        return this.actionType != null && !this.actionType.isBlank() ? this.actionType : null;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public int getLocked() {
        return this.locked;
    }

    public int getVotes() {
        return this.votes;
    }
}

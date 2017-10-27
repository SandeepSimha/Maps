package com.bbva.my.bbvacompasaplication.model;

public class Results
{
    private String id;

    private String place_id;

    private String icon;

    private String name;

    private String formatted_address;

    private String rating;

    private String[] types;

    private String reference;

    private Geometry geometry;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPlace_id ()
    {
        return place_id;
    }

    public void setPlace_id (String place_id)
    {
        this.place_id = place_id;
    }

    public String getIcon ()
    {
        return icon;
    }

    public void setIcon (String icon)
    {
        this.icon = icon;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFormatted_address ()
    {
        return formatted_address;
    }

    public void setFormatted_address (String formatted_address)
    {
        this.formatted_address = formatted_address;
    }

    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public String[] getTypes ()
    {
        return types;
    }

    public void setTypes (String[] types)
    {
        this.types = types;
    }

    public String getReference ()
    {
        return reference;
    }

    public void setReference (String reference)
    {
        this.reference = reference;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", place_id = "+place_id+", icon = "+icon+", name = "+name+", formatted_address = "+formatted_address+", rating = "+rating+", types = "+types+", reference = "+reference+", geometry = "+geometry+"]";
    }
}
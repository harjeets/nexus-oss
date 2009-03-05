package org.sonatype.plugin.nexus.testenvironment;

public class MavenArtifact
{

    private String artifactId;

    private String classifier;

    private String groupId;

    private String type;

    public MavenArtifact()
    {
        super();
    }

    public MavenArtifact( String groupId, String artifactId )
    {
        this();
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public MavenArtifact( String groupId, String artifactId, String classifier, String type )
    {
        this( groupId, artifactId );
        this.classifier = classifier;
        this.type = type;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getClassifier()
    {
        return classifier;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getType()
    {
        return type;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public void setClassifier( String classifier )
    {
        this.classifier = classifier;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append( groupId ).append( ':' ).//
        append( artifactId ).append( ':' ).//
        append( classifier ).append( ':' ).//
        append( type ).//
        toString();
    }
}

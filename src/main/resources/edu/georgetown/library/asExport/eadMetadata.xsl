<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" />
    
    <xsl:param name="creator"/>
    <xsl:param name="rights"/>
    <xsl:param name="author"/>
    <xsl:param name="uri"/>
    
    <xsl:template match="/ead">
      <dublin_core schema="dc">
        <dcvalue element="title" qualifier="none">
          <xsl:value-of select="//titlestmt/titleproper/text()"/>
        </dcvalue>
        
        <dcvalue element="identifier" qualifier="other">
          <xsl:value-of select="//unitid"/>
        </dcvalue>
 
        <dcvalue element="type">
          <xsl:text>Finding Aid</xsl:text>
        </dcvalue>
        
        <dcvalue element="creator">
          <xsl:value-of select="$creator"/>
        </dcvalue>

        <dcvalue element="contributor" qualifier="author">
          <xsl:value-of select="$author"/>
        </dcvalue>
        
        <dcvalue element="rights">
          <xsl:value-of select="$rights"/>
        </dcvalue>
        
        <dcvalue element="relation" qualifier="uri">
          <xsl:value-of select="$uri"/>
        </dcvalue>

        <dcvalue element="date" qualifer="created">
          <xsl:choose>
            <xsl:when test="//unitdate[@type='inclusive']">
              <xsl:value-of select="//unitdate[@type='inclusive']"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="//unitdate/text()"/>
            </xsl:otherwise>
          </xsl:choose>
        </dcvalue>
        
        <xsl:for-each select="//controlaccess/subject|//controlaccess/genreform|//controlaccess/*">
          <dcvalue element="subject">
            <xsl:value-of select="text()"/>
          </dcvalue>
        </xsl:for-each>
        
        <xsl:for-each select="//scopecontent/p">
            <dcvalue element="description]">
              <xsl:value-of select="normalize-space(text())"/>
            </dcvalue>
        </xsl:for-each>
        
      </dublin_core>
    </xsl:template>
   
</xsl:stylesheet>

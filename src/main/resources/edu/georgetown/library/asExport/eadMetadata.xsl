<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" />
    
    <xsl:param name="creator"/>
    <xsl:param name="rights"/>
    <xsl:param name="author"/>
    <xsl:param name="uri"/>
    
    <xsl:template match="/ead">
      <dublin_core schema="dc">
        <xsl:if test="//titlestmt/titleproper/text()">
          <dcvalue element="title" qualifier="none">
            <xsl:value-of select="//titlestmt/titleproper/text()"/>
          </dcvalue>
        </xsl:if>
        
        <xsl:if test="//unitid/text()">
          <dcvalue element="identifier" qualifier="other">
            <xsl:value-of select="//unitid/text()"/>
          </dcvalue>
        </xsl:if>
        
        <dcvalue element="type">
          <xsl:text>Finding Aid</xsl:text>
        </dcvalue>
        
        <xsl:if test="$creator">
          <dcvalue element="creator">
            <xsl:value-of select="$creator"/>
          </dcvalue>
        </xsl:if>
        
        <xsl:if test="$author">
          <dcvalue element="contributor" qualifier="author">
            <xsl:value-of select="$author"/>
          </dcvalue>
        </xsl:if>
        
        <xsl:if test="$rights">
          <dcvalue element="rights">
            <xsl:value-of select="$rights"/>
          </dcvalue>
        </xsl:if>
        
        <xsl:if test="$uri">
          <dcvalue element="relation" qualifier="uri">
            <xsl:value-of select="$uri"/>
          </dcvalue>
        </xsl:if>
        
        <xsl:choose>
          <xsl:when test="//unitdate[@type='inclusive']">
            <dcvalue element="date" qualifer="created">
              <xsl:value-of select="//unitdate[@type='inclusive']"/>
            </dcvalue>
          </xsl:when>
          <xsl:when test="//unitdate/text()">
            <dcvalue element="date" qualifer="created">
              <xsl:value-of select="//unitdate/text()"/>
            </dcvalue>
          </xsl:when>
        </xsl:choose>
        
        <xsl:for-each select="//controlaccess/subject|//controlaccess/genreform|//controlaccess/*">
          <xsl:if test="text()">
            <dcvalue element="subject">
              <xsl:value-of select="text()"/>
            </dcvalue>
          </xsl:if>
        </xsl:for-each>
        
        <xsl:for-each select="//scopecontent/p">
          <xsl:if test="text()">
            <dcvalue element="description]">
              <xsl:value-of select="normalize-space(text())"/>
            </dcvalue>
          </xsl:if>
        </xsl:for-each>
        
      </dublin_core>
    </xsl:template>
   
</xsl:stylesheet>

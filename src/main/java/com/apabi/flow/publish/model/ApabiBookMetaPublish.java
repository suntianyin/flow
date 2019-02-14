package com.apabi.flow.publish.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/8/15 16:39
 **/
public class ApabiBookMetaPublish {
    private String metaId;

    private String idType;

    private Integer saleStatus;

    private String language;

    private String title;

    private String subtitle;

    private String creator;

    private String authorIntro;

    private String creatorWord;

    private String creatorId;

    private String contributor;

    private String contributorWord;

    private String contributorId;

    private String translator;

    private String translatorId;

    private String originTitle;

    private String alternativeTitle;

    private String editionOrder;

    private String editionNote;

    private String place;

    private String publisher;

    private String publisherId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private String issuedDate;

    private String endIssuedDate;

    private String issuedDateDesc;

    private String abstract_;

    private String subject;

    private String preface;

    private String reader;

    private String classCode;

    private String apabiClass;

    private String type;

    private String isbn;

    private String isbn10;

    private String isbn13;

    private String paperPrice;

    private String ebookPrice;

    private String foreignPrice;

    private String foreignPriceType;

    private String paperPriceDesc;

    private String binding;

    private String illustration;

    private String pressOrder;

    private String editor;

    private String relation;

    private String relationId;

    private String volume;

    private Integer isSeries;

    private String volumesCount;

    private String isAllublished;

    private String notes;

    private String podPrice;

    private String podPriceType;

    private String podPricePageUnit;

    private Integer drId;

    private String doubanId;

    private String amazonId;

    private String calisId;

    private String nlibraryId;

    private String dataSource;

    private String qualityRating;

    private String qrType;

    private String cebxObjId;

    private String cebxFileSize;

    private Integer hasCebx;

    private String cebxPage;

    private String complexOid;

    private String reditor;

    private String departmentId;

    private String coverUrl;

    private String imgHeigth;

    private String imgWidth;

    private String covObjId;

    private String thumImgUrl;

    private String thumImgSize;

    private String mediumCover;

    private Integer hasFlow;

    private String postScript;

    private String isOptimize;

    private String donor;

    private String libraryId;

    private String bookId;

    private String styleClass;

    private String styleUrl;

    private String chapterNum;

    private String bookPages;

    private String tags;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT+8")
    private Date updateTime;

    private String contentNum;

    private String foamatCatalog;

    private String streamCatalog;

    private Integer hasPublish;

    private Integer isPublicCopyRight;

    private Integer isReadEpub;

    private String flowSource;

    private Integer isReadCebxFlow;

    public Integer getIsReadEpub() {
        return isReadEpub;
    }

    public void setIsReadEpub(Integer isReadEpub) {
        this.isReadEpub = isReadEpub;
    }

    public String getFlowSource() {
        return flowSource;
    }

    public void setFlowSource(String flowSource) {
        this.flowSource = flowSource;
    }

    public Integer getIsReadCebxFlow() {
        return isReadCebxFlow;
    }

    public void setIsReadCebxFlow(Integer isReadCebxFlow) {
        this.isReadCebxFlow = isReadCebxFlow;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String getCreatorWord() {
        return creatorWord;
    }

    public void setCreatorWord(String creatorWord) {
        this.creatorWord = creatorWord;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributorWord() {
        return contributorWord;
    }

    public void setContributorWord(String contributorWord) {
        this.contributorWord = contributorWord;
    }

    public String getContributorId() {
        return contributorId;
    }

    public void setContributorId(String contributorId) {
        this.contributorId = contributorId;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getTranslatorId() {
        return translatorId;
    }

    public void setTranslatorId(String translatorId) {
        this.translatorId = translatorId;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getEditionOrder() {
        return editionOrder;
    }

    public void setEditionOrder(String editionOrder) {
        this.editionOrder = editionOrder;
    }

    public String getEditionNote() {
        return editionNote;
    }

    public void setEditionNote(String editionNote) {
        this.editionNote = editionNote;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getEndIssuedDate() {
        return endIssuedDate;
    }

    public void setEndIssuedDate(String endIssuedDate) {
        this.endIssuedDate = endIssuedDate;
    }

    public String getIssuedDateDesc() {
        return issuedDateDesc;
    }

    public void setIssuedDateDesc(String issuedDateDesc) {
        this.issuedDateDesc = issuedDateDesc;
    }

    public String getAbstract_() {
        return abstract_;
    }

    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getApabiClass() {
        return apabiClass;
    }

    public void setApabiClass(String apabiClass) {
        this.apabiClass = apabiClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getPaperPrice() {
        return paperPrice;
    }

    public void setPaperPrice(String paperPrice) {
        this.paperPrice = paperPrice;
    }

    public String getEbookPrice() {
        return ebookPrice;
    }

    public void setEbookPrice(String ebookPrice) {
        this.ebookPrice = ebookPrice;
    }

    public String getForeignPrice() {
        return foreignPrice;
    }

    public void setForeignPrice(String foreignPrice) {
        this.foreignPrice = foreignPrice;
    }

    public String getForeignPriceType() {
        return foreignPriceType;
    }

    public void setForeignPriceType(String foreignPriceType) {
        this.foreignPriceType = foreignPriceType;
    }

    public String getPaperPriceDesc() {
        return paperPriceDesc;
    }

    public void setPaperPriceDesc(String paperPriceDesc) {
        this.paperPriceDesc = paperPriceDesc;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public String getPressOrder() {
        return pressOrder;
    }

    public void setPressOrder(String pressOrder) {
        this.pressOrder = pressOrder;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Integer getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(Integer isSeries) {
        this.isSeries = isSeries;
    }

    public String getVolumesCount() {
        return volumesCount;
    }

    public void setVolumesCount(String volumesCount) {
        this.volumesCount = volumesCount;
    }

    public String getIsAllublished() {
        return isAllublished;
    }

    public void setIsAllublished(String isAllublished) {
        this.isAllublished = isAllublished;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPodPrice() {
        return podPrice;
    }

    public void setPodPrice(String podPrice) {
        this.podPrice = podPrice;
    }

    public String getPodPriceType() {
        return podPriceType;
    }

    public void setPodPriceType(String podPriceType) {
        this.podPriceType = podPriceType;
    }

    public String getPodPricePageUnit() {
        return podPricePageUnit;
    }

    public void setPodPricePageUnit(String podPricePageUnit) {
        this.podPricePageUnit = podPricePageUnit;
    }

    public Integer getDrId() {
        return drId;
    }

    public void setDrId(Integer drId) {
        this.drId = drId;
    }

    public String getDoubanId() {
        return doubanId;
    }

    public void setDoubanId(String doubanId) {
        this.doubanId = doubanId;
    }

    public String getAmazonId() {
        return amazonId;
    }

    public void setAmazonId(String amazonId) {
        this.amazonId = amazonId;
    }

    public String getCalisId() {
        return calisId;
    }

    public void setCalisId(String calisId) {
        this.calisId = calisId;
    }

    public String getNlibraryId() {
        return nlibraryId;
    }

    public void setNlibraryId(String nlibraryId) {
        this.nlibraryId = nlibraryId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(String qualityRating) {
        this.qualityRating = qualityRating;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getCebxObjId() {
        return cebxObjId;
    }

    public void setCebxObjId(String cebxObjId) {
        this.cebxObjId = cebxObjId;
    }

    public String getCebxFileSize() {
        return cebxFileSize;
    }

    public void setCebxFileSize(String cebxFileSize) {
        this.cebxFileSize = cebxFileSize;
    }

    public Integer getHasCebx() {
        return hasCebx;
    }

    public void setHasCebx(Integer hasCebx) {
        this.hasCebx = hasCebx;
    }

    public String getCebxPage() {
        return cebxPage;
    }

    public void setCebxPage(String cebxPage) {
        this.cebxPage = cebxPage;
    }

    public String getComplexOid() {
        return complexOid;
    }

    public void setComplexOid(String complexOid) {
        this.complexOid = complexOid;
    }

    public String getReditor() {
        return reditor;
    }

    public void setReditor(String reditor) {
        this.reditor = reditor;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getImgHeigth() {
        return imgHeigth;
    }

    public void setImgHeigth(String imgHeigth) {
        this.imgHeigth = imgHeigth;
    }

    public String getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(String imgWidth) {
        this.imgWidth = imgWidth;
    }

    public String getCovObjId() {
        return covObjId;
    }

    public void setCovObjId(String covObjId) {
        this.covObjId = covObjId;
    }

    public String getThumImgUrl() {
        return thumImgUrl;
    }

    public void setThumImgUrl(String thumImgUrl) {
        this.thumImgUrl = thumImgUrl;
    }

    public String getThumImgSize() {
        return thumImgSize;
    }

    public void setThumImgSize(String thumImgSize) {
        this.thumImgSize = thumImgSize;
    }

    public String getMediumCover() {
        return mediumCover;
    }

    public void setMediumCover(String mediumCover) {
        this.mediumCover = mediumCover;
    }

    public Integer getHasFlow() {
        return hasFlow;
    }

    public void setHasFlow(Integer hasFlow) {
        this.hasFlow = hasFlow;
    }

    public String getPostScript() {
        return postScript;
    }

    public void setPostScript(String postScript) {
        this.postScript = postScript;
    }

    public String getIsOptimize() {
        return isOptimize;
    }

    public void setIsOptimize(String isOptimize) {
        this.isOptimize = isOptimize;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(String libraryId) {
        this.libraryId = libraryId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public String getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(String chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getBookPages() {
        return bookPages;
    }

    public void setBookPages(String bookPages) {
        this.bookPages = bookPages;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getContentNum() {
        return contentNum;
    }

    public void setContentNum(String contentNum) {
        this.contentNum = contentNum;
    }

    public String getFoamatCatalog() {
        return foamatCatalog;
    }

    public void setFoamatCatalog(String foamatCatalog) {
        this.foamatCatalog = foamatCatalog;
    }

    public String getStreamCatalog() {
        return streamCatalog;
    }

    public void setStreamCatalog(String streamCatalog) {
        this.streamCatalog = streamCatalog;
    }

    public Integer getHasPublish() {
        return hasPublish;
    }

    public void setHasPublish(Integer hasPublish) {
        this.hasPublish = hasPublish;
    }

    public Integer getIsPublicCopyRight() {
        return isPublicCopyRight;
    }

    public void setIsPublicCopyRight(Integer isPublicCopyRight) {
        this.isPublicCopyRight = isPublicCopyRight;
    }

    @Override
    public String toString() {
        return "ApabiBookMetaPublish{" +
                "metaId='" + metaId + '\'' +
                ", idType='" + idType + '\'' +
                ", saleStatus=" + saleStatus +
                ", language='" + language + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", creator='" + creator + '\'' +
                ", authorIntro='" + authorIntro + '\'' +
                ", creatorWord='" + creatorWord + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", contributor='" + contributor + '\'' +
                ", contributorWord='" + contributorWord + '\'' +
                ", contributorId='" + contributorId + '\'' +
                ", translator='" + translator + '\'' +
                ", translatorId='" + translatorId + '\'' +
                ", originTitle='" + originTitle + '\'' +
                ", alternativeTitle='" + alternativeTitle + '\'' +
                ", editionOrder='" + editionOrder + '\'' +
                ", editionNote='" + editionNote + '\'' +
                ", place='" + place + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publisherId='" + publisherId + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                ", endIssuedDate='" + endIssuedDate + '\'' +
                ", issuedDateDesc='" + issuedDateDesc + '\'' +
                ", abstract_='" + abstract_ + '\'' +
                ", subject='" + subject + '\'' +
                ", preface='" + preface + '\'' +
                ", reader='" + reader + '\'' +
                ", classCode='" + classCode + '\'' +
                ", apabiClass='" + apabiClass + '\'' +
                ", type='" + type + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isbn10='" + isbn10 + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", paperPrice='" + paperPrice + '\'' +
                ", ebookPrice='" + ebookPrice + '\'' +
                ", foreignPrice='" + foreignPrice + '\'' +
                ", foreignPriceType='" + foreignPriceType + '\'' +
                ", paperPriceDesc='" + paperPriceDesc + '\'' +
                ", binding='" + binding + '\'' +
                ", illustration='" + illustration + '\'' +
                ", pressOrder='" + pressOrder + '\'' +
                ", editor='" + editor + '\'' +
                ", relation='" + relation + '\'' +
                ", relationId='" + relationId + '\'' +
                ", volume='" + volume + '\'' +
                ", isSeries=" + isSeries +
                ", volumesCount='" + volumesCount + '\'' +
                ", isAllublished='" + isAllublished + '\'' +
                ", notes='" + notes + '\'' +
                ", podPrice='" + podPrice + '\'' +
                ", podPriceType='" + podPriceType + '\'' +
                ", podPricePageUnit='" + podPricePageUnit + '\'' +
                ", drId=" + drId +
                ", doubanId='" + doubanId + '\'' +
                ", amazonId='" + amazonId + '\'' +
                ", calisId='" + calisId + '\'' +
                ", nlibraryId='" + nlibraryId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", qualityRating='" + qualityRating + '\'' +
                ", qrType='" + qrType + '\'' +
                ", cebxObjId='" + cebxObjId + '\'' +
                ", cebxFileSize='" + cebxFileSize + '\'' +
                ", hasCebx=" + hasCebx +
                ", cebxPage='" + cebxPage + '\'' +
                ", complexOid='" + complexOid + '\'' +
                ", reditor='" + reditor + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", imgHeigth='" + imgHeigth + '\'' +
                ", imgWidth='" + imgWidth + '\'' +
                ", covObjId='" + covObjId + '\'' +
                ", thumImgUrl='" + thumImgUrl + '\'' +
                ", thumImgSize='" + thumImgSize + '\'' +
                ", mediumCover='" + mediumCover + '\'' +
                ", hasFlow=" + hasFlow +
                ", postScript='" + postScript + '\'' +
                ", isOptimize='" + isOptimize + '\'' +
                ", donor='" + donor + '\'' +
                ", libraryId='" + libraryId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", styleClass='" + styleClass + '\'' +
                ", styleUrl='" + styleUrl + '\'' +
                ", chapterNum='" + chapterNum + '\'' +
                ", bookPages='" + bookPages + '\'' +
                ", tags='" + tags + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", contentNum='" + contentNum + '\'' +
                ", foamatCatalog='" + foamatCatalog + '\'' +
                ", streamCatalog='" + streamCatalog + '\'' +
                ", hasPublish=" + hasPublish +
                ", isPublicCopyRight=" + isPublicCopyRight +
                ", isReadEpub=" + isReadEpub +
                ", flowSource='" + flowSource + '\'' +
                ", isReadCebxFlow=" + isReadCebxFlow +
                '}';
    }
}

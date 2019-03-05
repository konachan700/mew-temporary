// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: record.proto

public final class PassRecordDTO {
  private PassRecordDTO() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public interface recordOrBuilder extends
      // @@protoc_insertion_point(interface_extends:record)
      com.google.protobuf.MessageLiteOrBuilder {

    /**
     * <code>optional int32 packageId = 1;</code>
     */
    int getPackageId();

    /**
     * <code>optional bool hasDigit = 2;</code>
     */
    boolean getHasDigit();

    /**
     * <code>optional bool hasAlphaUppercase = 3;</code>
     */
    boolean getHasAlphaUppercase();

    /**
     * <code>optional bool hasAlphaLowercase = 4;</code>
     */
    boolean getHasAlphaLowercase();

    /**
     * <code>optional bool hasSymbols = 5;</code>
     */
    boolean getHasSymbols();

    /**
     * <code>optional string displayName = 6;</code>
     */
    java.lang.String getDisplayName();
    /**
     * <code>optional string displayName = 6;</code>
     */
    com.google.protobuf.ByteString
        getDisplayNameBytes();

    /**
     * <code>optional string login = 7;</code>
     */
    java.lang.String getLogin();
    /**
     * <code>optional string login = 7;</code>
     */
    com.google.protobuf.ByteString
        getLoginBytes();

    /**
     * <code>optional string url = 8;</code>
     */
    java.lang.String getUrl();
    /**
     * <code>optional string url = 8;</code>
     */
    com.google.protobuf.ByteString
        getUrlBytes();

    /**
     * <code>optional int64 hwID = 9;</code>
     */
    long getHwID();

    /**
     * <code>optional int32 maxLen = 10;</code>
     */
    int getMaxLen();

    /**
     * <code>optional int32 minLen = 11;</code>
     */
    int getMinLen();

    /**
     * <code>optional int32 type = 12;</code>
     */
    int getType();
  }
  /**
   * Protobuf type {@code record}
   */
  public  static final class record extends
      com.google.protobuf.GeneratedMessageLite<
          record, record.Builder> implements
      // @@protoc_insertion_point(message_implements:record)
      recordOrBuilder {
    private record() {
      displayName_ = "";
      login_ = "";
      url_ = "";
    }
    public static final int PACKAGEID_FIELD_NUMBER = 1;
    private int packageId_;
    /**
     * <code>optional int32 packageId = 1;</code>
     */
    public int getPackageId() {
      return packageId_;
    }
    /**
     * <code>optional int32 packageId = 1;</code>
     */
    private void setPackageId(int value) {
      
      packageId_ = value;
    }
    /**
     * <code>optional int32 packageId = 1;</code>
     */
    private void clearPackageId() {
      
      packageId_ = 0;
    }

    public static final int HASDIGIT_FIELD_NUMBER = 2;
    private boolean hasDigit_;
    /**
     * <code>optional bool hasDigit = 2;</code>
     */
    public boolean getHasDigit() {
      return hasDigit_;
    }
    /**
     * <code>optional bool hasDigit = 2;</code>
     */
    private void setHasDigit(boolean value) {
      
      hasDigit_ = value;
    }
    /**
     * <code>optional bool hasDigit = 2;</code>
     */
    private void clearHasDigit() {
      
      hasDigit_ = false;
    }

    public static final int HASALPHAUPPERCASE_FIELD_NUMBER = 3;
    private boolean hasAlphaUppercase_;
    /**
     * <code>optional bool hasAlphaUppercase = 3;</code>
     */
    public boolean getHasAlphaUppercase() {
      return hasAlphaUppercase_;
    }
    /**
     * <code>optional bool hasAlphaUppercase = 3;</code>
     */
    private void setHasAlphaUppercase(boolean value) {
      
      hasAlphaUppercase_ = value;
    }
    /**
     * <code>optional bool hasAlphaUppercase = 3;</code>
     */
    private void clearHasAlphaUppercase() {
      
      hasAlphaUppercase_ = false;
    }

    public static final int HASALPHALOWERCASE_FIELD_NUMBER = 4;
    private boolean hasAlphaLowercase_;
    /**
     * <code>optional bool hasAlphaLowercase = 4;</code>
     */
    public boolean getHasAlphaLowercase() {
      return hasAlphaLowercase_;
    }
    /**
     * <code>optional bool hasAlphaLowercase = 4;</code>
     */
    private void setHasAlphaLowercase(boolean value) {
      
      hasAlphaLowercase_ = value;
    }
    /**
     * <code>optional bool hasAlphaLowercase = 4;</code>
     */
    private void clearHasAlphaLowercase() {
      
      hasAlphaLowercase_ = false;
    }

    public static final int HASSYMBOLS_FIELD_NUMBER = 5;
    private boolean hasSymbols_;
    /**
     * <code>optional bool hasSymbols = 5;</code>
     */
    public boolean getHasSymbols() {
      return hasSymbols_;
    }
    /**
     * <code>optional bool hasSymbols = 5;</code>
     */
    private void setHasSymbols(boolean value) {
      
      hasSymbols_ = value;
    }
    /**
     * <code>optional bool hasSymbols = 5;</code>
     */
    private void clearHasSymbols() {
      
      hasSymbols_ = false;
    }

    public static final int DISPLAYNAME_FIELD_NUMBER = 6;
    private java.lang.String displayName_;
    /**
     * <code>optional string displayName = 6;</code>
     */
    public java.lang.String getDisplayName() {
      return displayName_;
    }
    /**
     * <code>optional string displayName = 6;</code>
     */
    public com.google.protobuf.ByteString
        getDisplayNameBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(displayName_);
    }
    /**
     * <code>optional string displayName = 6;</code>
     */
    private void setDisplayName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      displayName_ = value;
    }
    /**
     * <code>optional string displayName = 6;</code>
     */
    private void clearDisplayName() {
      
      displayName_ = getDefaultInstance().getDisplayName();
    }
    /**
     * <code>optional string displayName = 6;</code>
     */
    private void setDisplayNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      displayName_ = value.toStringUtf8();
    }

    public static final int LOGIN_FIELD_NUMBER = 7;
    private java.lang.String login_;
    /**
     * <code>optional string login = 7;</code>
     */
    public java.lang.String getLogin() {
      return login_;
    }
    /**
     * <code>optional string login = 7;</code>
     */
    public com.google.protobuf.ByteString
        getLoginBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(login_);
    }
    /**
     * <code>optional string login = 7;</code>
     */
    private void setLogin(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      login_ = value;
    }
    /**
     * <code>optional string login = 7;</code>
     */
    private void clearLogin() {
      
      login_ = getDefaultInstance().getLogin();
    }
    /**
     * <code>optional string login = 7;</code>
     */
    private void setLoginBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      login_ = value.toStringUtf8();
    }

    public static final int URL_FIELD_NUMBER = 8;
    private java.lang.String url_;
    /**
     * <code>optional string url = 8;</code>
     */
    public java.lang.String getUrl() {
      return url_;
    }
    /**
     * <code>optional string url = 8;</code>
     */
    public com.google.protobuf.ByteString
        getUrlBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(url_);
    }
    /**
     * <code>optional string url = 8;</code>
     */
    private void setUrl(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      url_ = value;
    }
    /**
     * <code>optional string url = 8;</code>
     */
    private void clearUrl() {
      
      url_ = getDefaultInstance().getUrl();
    }
    /**
     * <code>optional string url = 8;</code>
     */
    private void setUrlBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      url_ = value.toStringUtf8();
    }

    public static final int HWID_FIELD_NUMBER = 9;
    private long hwID_;
    /**
     * <code>optional int64 hwID = 9;</code>
     */
    public long getHwID() {
      return hwID_;
    }
    /**
     * <code>optional int64 hwID = 9;</code>
     */
    private void setHwID(long value) {
      
      hwID_ = value;
    }
    /**
     * <code>optional int64 hwID = 9;</code>
     */
    private void clearHwID() {
      
      hwID_ = 0L;
    }

    public static final int MAXLEN_FIELD_NUMBER = 10;
    private int maxLen_;
    /**
     * <code>optional int32 maxLen = 10;</code>
     */
    public int getMaxLen() {
      return maxLen_;
    }
    /**
     * <code>optional int32 maxLen = 10;</code>
     */
    private void setMaxLen(int value) {
      
      maxLen_ = value;
    }
    /**
     * <code>optional int32 maxLen = 10;</code>
     */
    private void clearMaxLen() {
      
      maxLen_ = 0;
    }

    public static final int MINLEN_FIELD_NUMBER = 11;
    private int minLen_;
    /**
     * <code>optional int32 minLen = 11;</code>
     */
    public int getMinLen() {
      return minLen_;
    }
    /**
     * <code>optional int32 minLen = 11;</code>
     */
    private void setMinLen(int value) {
      
      minLen_ = value;
    }
    /**
     * <code>optional int32 minLen = 11;</code>
     */
    private void clearMinLen() {
      
      minLen_ = 0;
    }

    public static final int TYPE_FIELD_NUMBER = 12;
    private int type_;
    /**
     * <code>optional int32 type = 12;</code>
     */
    public int getType() {
      return type_;
    }
    /**
     * <code>optional int32 type = 12;</code>
     */
    private void setType(int value) {
      
      type_ = value;
    }
    /**
     * <code>optional int32 type = 12;</code>
     */
    private void clearType() {
      
      type_ = 0;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (packageId_ != 0) {
        output.writeInt32(1, packageId_);
      }
      if (hasDigit_ != false) {
        output.writeBool(2, hasDigit_);
      }
      if (hasAlphaUppercase_ != false) {
        output.writeBool(3, hasAlphaUppercase_);
      }
      if (hasAlphaLowercase_ != false) {
        output.writeBool(4, hasAlphaLowercase_);
      }
      if (hasSymbols_ != false) {
        output.writeBool(5, hasSymbols_);
      }
      if (!displayName_.isEmpty()) {
        output.writeString(6, getDisplayName());
      }
      if (!login_.isEmpty()) {
        output.writeString(7, getLogin());
      }
      if (!url_.isEmpty()) {
        output.writeString(8, getUrl());
      }
      if (hwID_ != 0L) {
        output.writeInt64(9, hwID_);
      }
      if (maxLen_ != 0) {
        output.writeInt32(10, maxLen_);
      }
      if (minLen_ != 0) {
        output.writeInt32(11, minLen_);
      }
      if (type_ != 0) {
        output.writeInt32(12, type_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (packageId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, packageId_);
      }
      if (hasDigit_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, hasDigit_);
      }
      if (hasAlphaUppercase_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(3, hasAlphaUppercase_);
      }
      if (hasAlphaLowercase_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(4, hasAlphaLowercase_);
      }
      if (hasSymbols_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(5, hasSymbols_);
      }
      if (!displayName_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(6, getDisplayName());
      }
      if (!login_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(7, getLogin());
      }
      if (!url_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(8, getUrl());
      }
      if (hwID_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(9, hwID_);
      }
      if (maxLen_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(10, maxLen_);
      }
      if (minLen_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(11, minLen_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(12, type_);
      }
      memoizedSerializedSize = size;
      return size;
    }

    public static PassRecordDTO.record parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static PassRecordDTO.record parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static PassRecordDTO.record parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static PassRecordDTO.record parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static PassRecordDTO.record parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static PassRecordDTO.record parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static PassRecordDTO.record parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }
    public static PassRecordDTO.record parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static PassRecordDTO.record parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static PassRecordDTO.record parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(PassRecordDTO.record prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    /**
     * Protobuf type {@code record}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          PassRecordDTO.record, Builder> implements
        // @@protoc_insertion_point(builder_implements:record)
        PassRecordDTO.recordOrBuilder {
      // Construct using PassRecordDTO.record.newBuilder()
      private Builder() {
        super(DEFAULT_INSTANCE);
      }


      /**
       * <code>optional int32 packageId = 1;</code>
       */
      public int getPackageId() {
        return instance.getPackageId();
      }
      /**
       * <code>optional int32 packageId = 1;</code>
       */
      public Builder setPackageId(int value) {
        copyOnWrite();
        instance.setPackageId(value);
        return this;
      }
      /**
       * <code>optional int32 packageId = 1;</code>
       */
      public Builder clearPackageId() {
        copyOnWrite();
        instance.clearPackageId();
        return this;
      }

      /**
       * <code>optional bool hasDigit = 2;</code>
       */
      public boolean getHasDigit() {
        return instance.getHasDigit();
      }
      /**
       * <code>optional bool hasDigit = 2;</code>
       */
      public Builder setHasDigit(boolean value) {
        copyOnWrite();
        instance.setHasDigit(value);
        return this;
      }
      /**
       * <code>optional bool hasDigit = 2;</code>
       */
      public Builder clearHasDigit() {
        copyOnWrite();
        instance.clearHasDigit();
        return this;
      }

      /**
       * <code>optional bool hasAlphaUppercase = 3;</code>
       */
      public boolean getHasAlphaUppercase() {
        return instance.getHasAlphaUppercase();
      }
      /**
       * <code>optional bool hasAlphaUppercase = 3;</code>
       */
      public Builder setHasAlphaUppercase(boolean value) {
        copyOnWrite();
        instance.setHasAlphaUppercase(value);
        return this;
      }
      /**
       * <code>optional bool hasAlphaUppercase = 3;</code>
       */
      public Builder clearHasAlphaUppercase() {
        copyOnWrite();
        instance.clearHasAlphaUppercase();
        return this;
      }

      /**
       * <code>optional bool hasAlphaLowercase = 4;</code>
       */
      public boolean getHasAlphaLowercase() {
        return instance.getHasAlphaLowercase();
      }
      /**
       * <code>optional bool hasAlphaLowercase = 4;</code>
       */
      public Builder setHasAlphaLowercase(boolean value) {
        copyOnWrite();
        instance.setHasAlphaLowercase(value);
        return this;
      }
      /**
       * <code>optional bool hasAlphaLowercase = 4;</code>
       */
      public Builder clearHasAlphaLowercase() {
        copyOnWrite();
        instance.clearHasAlphaLowercase();
        return this;
      }

      /**
       * <code>optional bool hasSymbols = 5;</code>
       */
      public boolean getHasSymbols() {
        return instance.getHasSymbols();
      }
      /**
       * <code>optional bool hasSymbols = 5;</code>
       */
      public Builder setHasSymbols(boolean value) {
        copyOnWrite();
        instance.setHasSymbols(value);
        return this;
      }
      /**
       * <code>optional bool hasSymbols = 5;</code>
       */
      public Builder clearHasSymbols() {
        copyOnWrite();
        instance.clearHasSymbols();
        return this;
      }

      /**
       * <code>optional string displayName = 6;</code>
       */
      public java.lang.String getDisplayName() {
        return instance.getDisplayName();
      }
      /**
       * <code>optional string displayName = 6;</code>
       */
      public com.google.protobuf.ByteString
          getDisplayNameBytes() {
        return instance.getDisplayNameBytes();
      }
      /**
       * <code>optional string displayName = 6;</code>
       */
      public Builder setDisplayName(
          java.lang.String value) {
        copyOnWrite();
        instance.setDisplayName(value);
        return this;
      }
      /**
       * <code>optional string displayName = 6;</code>
       */
      public Builder clearDisplayName() {
        copyOnWrite();
        instance.clearDisplayName();
        return this;
      }
      /**
       * <code>optional string displayName = 6;</code>
       */
      public Builder setDisplayNameBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setDisplayNameBytes(value);
        return this;
      }

      /**
       * <code>optional string login = 7;</code>
       */
      public java.lang.String getLogin() {
        return instance.getLogin();
      }
      /**
       * <code>optional string login = 7;</code>
       */
      public com.google.protobuf.ByteString
          getLoginBytes() {
        return instance.getLoginBytes();
      }
      /**
       * <code>optional string login = 7;</code>
       */
      public Builder setLogin(
          java.lang.String value) {
        copyOnWrite();
        instance.setLogin(value);
        return this;
      }
      /**
       * <code>optional string login = 7;</code>
       */
      public Builder clearLogin() {
        copyOnWrite();
        instance.clearLogin();
        return this;
      }
      /**
       * <code>optional string login = 7;</code>
       */
      public Builder setLoginBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setLoginBytes(value);
        return this;
      }

      /**
       * <code>optional string url = 8;</code>
       */
      public java.lang.String getUrl() {
        return instance.getUrl();
      }
      /**
       * <code>optional string url = 8;</code>
       */
      public com.google.protobuf.ByteString
          getUrlBytes() {
        return instance.getUrlBytes();
      }
      /**
       * <code>optional string url = 8;</code>
       */
      public Builder setUrl(
          java.lang.String value) {
        copyOnWrite();
        instance.setUrl(value);
        return this;
      }
      /**
       * <code>optional string url = 8;</code>
       */
      public Builder clearUrl() {
        copyOnWrite();
        instance.clearUrl();
        return this;
      }
      /**
       * <code>optional string url = 8;</code>
       */
      public Builder setUrlBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setUrlBytes(value);
        return this;
      }

      /**
       * <code>optional int64 hwID = 9;</code>
       */
      public long getHwID() {
        return instance.getHwID();
      }
      /**
       * <code>optional int64 hwID = 9;</code>
       */
      public Builder setHwID(long value) {
        copyOnWrite();
        instance.setHwID(value);
        return this;
      }
      /**
       * <code>optional int64 hwID = 9;</code>
       */
      public Builder clearHwID() {
        copyOnWrite();
        instance.clearHwID();
        return this;
      }

      /**
       * <code>optional int32 maxLen = 10;</code>
       */
      public int getMaxLen() {
        return instance.getMaxLen();
      }
      /**
       * <code>optional int32 maxLen = 10;</code>
       */
      public Builder setMaxLen(int value) {
        copyOnWrite();
        instance.setMaxLen(value);
        return this;
      }
      /**
       * <code>optional int32 maxLen = 10;</code>
       */
      public Builder clearMaxLen() {
        copyOnWrite();
        instance.clearMaxLen();
        return this;
      }

      /**
       * <code>optional int32 minLen = 11;</code>
       */
      public int getMinLen() {
        return instance.getMinLen();
      }
      /**
       * <code>optional int32 minLen = 11;</code>
       */
      public Builder setMinLen(int value) {
        copyOnWrite();
        instance.setMinLen(value);
        return this;
      }
      /**
       * <code>optional int32 minLen = 11;</code>
       */
      public Builder clearMinLen() {
        copyOnWrite();
        instance.clearMinLen();
        return this;
      }

      /**
       * <code>optional int32 type = 12;</code>
       */
      public int getType() {
        return instance.getType();
      }
      /**
       * <code>optional int32 type = 12;</code>
       */
      public Builder setType(int value) {
        copyOnWrite();
        instance.setType(value);
        return this;
      }
      /**
       * <code>optional int32 type = 12;</code>
       */
      public Builder clearType() {
        copyOnWrite();
        instance.clearType();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:record)
    }
    protected final Object dynamicMethod(
        com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
        Object arg0, Object arg1) {
      switch (method) {
        case NEW_MUTABLE_INSTANCE: {
          return new PassRecordDTO.record();
        }
        case IS_INITIALIZED: {
          return DEFAULT_INSTANCE;
        }
        case MAKE_IMMUTABLE: {
          return null;
        }
        case NEW_BUILDER: {
          return new Builder();
        }
        case VISIT: {
          Visitor visitor = (Visitor) arg0;
          PassRecordDTO.record other = (PassRecordDTO.record) arg1;
          packageId_ = visitor.visitInt(packageId_ != 0, packageId_,
              other.packageId_ != 0, other.packageId_);
          hasDigit_ = visitor.visitBoolean(hasDigit_ != false, hasDigit_,
              other.hasDigit_ != false, other.hasDigit_);
          hasAlphaUppercase_ = visitor.visitBoolean(hasAlphaUppercase_ != false, hasAlphaUppercase_,
              other.hasAlphaUppercase_ != false, other.hasAlphaUppercase_);
          hasAlphaLowercase_ = visitor.visitBoolean(hasAlphaLowercase_ != false, hasAlphaLowercase_,
              other.hasAlphaLowercase_ != false, other.hasAlphaLowercase_);
          hasSymbols_ = visitor.visitBoolean(hasSymbols_ != false, hasSymbols_,
              other.hasSymbols_ != false, other.hasSymbols_);
          displayName_ = visitor.visitString(!displayName_.isEmpty(), displayName_,
              !other.displayName_.isEmpty(), other.displayName_);
          login_ = visitor.visitString(!login_.isEmpty(), login_,
              !other.login_.isEmpty(), other.login_);
          url_ = visitor.visitString(!url_.isEmpty(), url_,
              !other.url_.isEmpty(), other.url_);
          hwID_ = visitor.visitLong(hwID_ != 0L, hwID_,
              other.hwID_ != 0L, other.hwID_);
          maxLen_ = visitor.visitInt(maxLen_ != 0, maxLen_,
              other.maxLen_ != 0, other.maxLen_);
          minLen_ = visitor.visitInt(minLen_ != 0, minLen_,
              other.minLen_ != 0, other.minLen_);
          type_ = visitor.visitInt(type_ != 0, type_,
              other.type_ != 0, other.type_);
          if (visitor == com.google.protobuf.GeneratedMessageLite.MergeFromVisitor
              .INSTANCE) {
          }
          return this;
        }
        case MERGE_FROM_STREAM: {
          com.google.protobuf.CodedInputStream input =
              (com.google.protobuf.CodedInputStream) arg0;
          com.google.protobuf.ExtensionRegistryLite extensionRegistry =
              (com.google.protobuf.ExtensionRegistryLite) arg1;
          try {
            boolean done = false;
            while (!done) {
              int tag = input.readTag();
              switch (tag) {
                case 0:
                  done = true;
                  break;
                default: {
                  if (!input.skipField(tag)) {
                    done = true;
                  }
                  break;
                }
                case 8: {

                  packageId_ = input.readInt32();
                  break;
                }
                case 16: {

                  hasDigit_ = input.readBool();
                  break;
                }
                case 24: {

                  hasAlphaUppercase_ = input.readBool();
                  break;
                }
                case 32: {

                  hasAlphaLowercase_ = input.readBool();
                  break;
                }
                case 40: {

                  hasSymbols_ = input.readBool();
                  break;
                }
                case 50: {
                  String s = input.readStringRequireUtf8();

                  displayName_ = s;
                  break;
                }
                case 58: {
                  String s = input.readStringRequireUtf8();

                  login_ = s;
                  break;
                }
                case 66: {
                  String s = input.readStringRequireUtf8();

                  url_ = s;
                  break;
                }
                case 72: {

                  hwID_ = input.readInt64();
                  break;
                }
                case 80: {

                  maxLen_ = input.readInt32();
                  break;
                }
                case 88: {

                  minLen_ = input.readInt32();
                  break;
                }
                case 96: {

                  type_ = input.readInt32();
                  break;
                }
              }
            }
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw new RuntimeException(e.setUnfinishedMessage(this));
          } catch (java.io.IOException e) {
            throw new RuntimeException(
                new com.google.protobuf.InvalidProtocolBufferException(
                    e.getMessage()).setUnfinishedMessage(this));
          } finally {
          }
        }
        case GET_DEFAULT_INSTANCE: {
          return DEFAULT_INSTANCE;
        }
        case GET_PARSER: {
          if (PARSER == null) {    synchronized (PassRecordDTO.record.class) {
              if (PARSER == null) {
                PARSER = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
              }
            }
          }
          return PARSER;
        }
      }
      throw new UnsupportedOperationException();
    }


    // @@protoc_insertion_point(class_scope:record)
    private static final PassRecordDTO.record DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new record();
      DEFAULT_INSTANCE.makeImmutable();
    }

    public static PassRecordDTO.record getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static volatile com.google.protobuf.Parser<record> PARSER;

    public static com.google.protobuf.Parser<record> parser() {
      return DEFAULT_INSTANCE.getParserForType();
    }
  }


  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}

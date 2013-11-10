package com.datatorrent.contrib.couchdb;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <br>To insert an object in Couch Db, any concrete implementation of {@link AbstractCouchDBOutputOperator}  needs to
 * provide additional information about the tuple that can be saved in the database. This interface outlines the api
 * of the command that will be executed against Couch-db.</br>
 * <br></br>
 * <br>The command would either update or insert a document in the db. If the docId is already present then the existing
 * document will be updated or otherwise it will be created.</br>
 * <br></br>
 * <br>
 *   When a document already exists in the db then couch-db expects the tuple to have a revision greater than the revision
 *   in the database. So we allow tuples to have revision as null and in that case, the prior revision is fetched from
 *   the database and is set on the object using setRevision.
 * </br>
 */
public interface CouchDbUpdateCommand
{

  @Nullable
  @JsonProperty("_id")
  String getId();

  @Nullable
  @JsonProperty("_rev")
  String getRevision();

  /**
   * @return object that is compatible with {@link org.ektorp.CouchDbConnector}
   */
  @Nonnull
  Object getPayLoad();

  /**
   * Each document in couch db has a revision. This sets the revision of the corresponding
   * document object in memory.
   *
   * @param revision revision
   */
  @JsonProperty("_rev")
  void setRevision(String revision);

  /**
   * Each document in couch db has an id. This sets the id of the corresponding document
   * object in memory.
   *
   * @param id
   */
  @JsonProperty("_id")
  void setId(String id);
}

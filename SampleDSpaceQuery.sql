/*
http://stackoverflow.com/questions/22078456/output-to-csv-in-postgres-with-double-quotes
The following query ran into parsing issues, so I added the code to create the temp table.
*/

create temporary table asSync(
  coll_handle varchar(256),
  item_handle varchar(256),
  item_title text,
  finding_aid_url text,
  finding_aid_file_name text,
  finding_aid_export_date varchar(8)
);

insert into asSync(
  coll_handle,
  item_handle,
  item_title,
  finding_aid_url,
  finding_aid_file_name,
  finding_aid_export_date
)
select                                                      
  ch.handle,
  ih.handle,
  (
    select 
      imv.text_value
    from metadatavalue imv
    where
      imv.resource_id = i.item_id 
      and imv.resource_type_id = 2
      and imv.metadata_field_id = (
        select metadata_field_id 
        from metadatafieldregistry 
        where element='title' 
        and qualifier is null
      )
  ),
  mv.text_value, 
  (
    select 
      bitmv.text_value
    from metadatavalue bitmv
    inner join bundle2bitstream b2b
      on b2b.bitstream_id = bitmv.resource_id 
      and bitmv.resource_type_id = 0
      and bitmv.metadata_field_id = (
        select metadata_field_id 
        from metadatafieldregistry 
        where element='title' 
        and qualifier is null
      )
    inner join item2bundle i2b
      on i2b.bundle_id = b2b.bundle_id
      and i2b.item_id = i.item_id
    inner join metadatavalue bmv
      on bmv.resource_id = b2b.bundle_id 
      and bmv.resource_type_id = 1
      and bmv.text_value = 'ORIGINAL'
      and bmv.metadata_field_id = (
        select metadata_field_id 
        from metadatafieldregistry 
        where element='title' 
        and qualifier is null
      )
  ),
  (
    select 
      substring(bitmv.text_value from 'ead\..*\.([0-9]{8,8})\.pdf')
    from metadatavalue bitmv
    inner join bundle2bitstream b2b
      on b2b.bitstream_id = bitmv.resource_id 
      and bitmv.resource_type_id = 0
      and bitmv.metadata_field_id = (
        select metadata_field_id 
        from metadatafieldregistry 
        where element='title' 
        and qualifier is null
      )
    inner join item2bundle i2b
      on i2b.bundle_id = b2b.bundle_id
      and i2b.item_id = i.item_id
    inner join metadatavalue bmv
      on bmv.resource_id = b2b.bundle_id 
      and bmv.resource_type_id = 1
      and bmv.text_value = 'ORIGINAL'
      and bmv.metadata_field_id = (
        select metadata_field_id 
        from metadatafieldregistry 
        where element='title' 
        and qualifier is null
      )
  )
from metadatavalue mv
inner join handle ih
  on ih.resource_id = mv.resource_id and ih.resource_type_id = mv.resource_type_id
inner join item i
  on i.item_id = mv.resource_id
inner join handle ch
  on i.owning_collection = ch.resource_id and ch.resource_type_id = 3
where metadata_field_id = (
  select metadata_field_id 
  from metadatafieldregistry mfr
  where mfr.element = 'relation'
  and mfr.qualifier = 'uri'
)
and mv.text_value ~ '<<URL TO YOUR ARCHIVES SPACE INSTANCE>>'
and mv.resource_type_id = 2
;

/*
The following code seemed to fail when split over multiple lines
*/
\copy (select * from asSync) to '<<LOCAL PATH TO YOUR INVENTORY FILE>>' WITH (FORMAT CSV, HEADER TRUE, FORCE_QUOTE *)
;


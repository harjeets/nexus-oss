/**
 * A RoleManager is used to display assigned roles and privileges (optional) in a grid, with a toolbar
 * that has options to add roles/privileges (which will open up a new window that will show a specialized role/privilege list
 * grid that will allow for filtering/paging the lists and selection for addition) and to remove roles/privileges ( again opening
 * up a new window, where the user can simply deselect available items ) 
 *  
 * usePrivileges - boolean flag, if true will use the privileges along with the roles 
 * selectedRoleIds - role ids that are already assigned 
 * selectedPrivilegeIds - privilege ids that are already assigned
 * userId - userId which is used to properly retrieve the data for UI (most specifically the external roles)
 */
RoleManager = function(config) {
  var config = config || {};

  var defaultConfig = {
    header : false,
    trackMouseOver : false,
    loadMask : true,
    usePrivileges : true,
    selectedRoleIds : [],
    selectedPrivilegeIds : [],
    onlySelected : true,
    userId : null,
    hideHeaders : true,
    style : 'border:1px solid #B5B8C8'
  };
  //apply the config and defaults to 'this'
  Ext.apply(this, config, defaultConfig);

  //proxy for the store, to assign jsonData
  this.storeProxy = new Ext.data.HttpProxy({
        method : 'POST',
        url : Sonatype.config.servicePath + '/rolesAndPrivs',
        jsonData : {
          data : {
            onlySelected : this.onlySelected,
            noPrivileges : !this.usePrivileges,
            noRoles : false,
            selectedRoleIds : this.selectedRoleIds,
            selectedPrivilegeIds : this.selectedPrivilegeIds,
            userId : this.userId
          }
        }
      });

  //our remote resource, that will be supplying the paginated content
  this.store = new Ext.data.JsonStore({
        root : 'data',
        id : 'id',
        totalProperty : 'totalCount',
        remoteSort : true,
        proxy : this.storeProxy,
        fields : [{
              name : 'id'
            }, {
              name : 'type'
            }, {
              name : 'name',
              sortType : Ext.data.SortTypes.asUCString
            }, {
              name : 'description'
            }, {
              name : 'external'
            }]
      });

  this.store.setDefaultSort('name', 'asc');

  //setup the columns in the grid
  this.columns = [];

  this.columns.push({
        id : 'name',
        width : 100,
        header : 'Name',
        dataIndex : 'name',
        sortable : true,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
          switch (record.get('type'))
          {
            case 'role' :
              metaData.css = 'roleRow';
              break;
            case 'privilege' :
              metaData.css = 'privilegeRow';
              break;
          }
          return String.format('<img class="placeholder" src="{0}"/>', Ext.BLANK_IMAGE_URL) + (record.get('external') ? ('<b>' + value + '</b>') : value);
        }
      });

  this.autoExpandColumn = 'name';

  this.tbar = ['Roles' + (this.usePrivileges ? '/Privileges' : ''), '->', '-', {
        text : 'Add',
        handler : this.addHandler,
        scope : this
      }, '-', {
        text : 'Remove',
        handler : this.removeHandler,
        scope : this
      }];

  RoleManager.superclass.constructor.call(this, {});

  this.getView().scrollOffset = 1;
};

Ext.extend(RoleManager, Ext.grid.GridPanel, {
      addHandler : function() {
        this.roleSelectorWindow = new Ext.Window({
              modal : true,
              items : [{
                    xtype : 'roleselector',
                    name : 'roleSelector',
                    height : 400,
                    width : 750,
                    usePrivileges : this.usePrivileges,
                    hiddenRoleIds : this.getSelectedRoleIds(),
                    hiddenPrivilegeIds : this.getSelectedPrivilegeIds(),
                    modal : true,
                    title : 'Add Roles' + (this.usePrivileges ? ' and Privileges' : '')
                  }],
              buttonAlign : 'center',
              buttons : [{
                    handler : this.addOk,
                    text : 'OK',
                    scope : this
                  }, {
                    handler : this.addCancel,
                    text : 'Cancel',
                    scope : this
                  }]
            });
        this.roleSelectorWindow.show();
      },
      removeHandler : function() {
        var records = this.getSelectionModel().getSelections();

        if (records && records.length > 0)
        {
          for (var i = 0; i < records.length; i++)
          {
            if (records[i].get('type') == 'role')
            {
              this.selectedRoleIds.remove(records[i].get('id'));
            }
            else if (records[i].get('type') == 'privilege')
            {
              this.selectedPrivilegeIds.remove(records[i].get('id'));
            }
          }
          this.reloadStore();
          this.validate();
        }
      },
      addOk : function() {
        var roleSelector = this.roleSelectorWindow.find('name', 'roleSelector')[0];
        this.addSelectedRoleId(roleSelector.getSelectedRoleIds());
        this.addSelectedPrivilegeId(roleSelector.getSelectedPrivilegeIds());
        this.roleSelectorWindow.close();
        this.reloadStore();
      },
      addCancel : function() {
        this.roleSelectorWindow.close();
      },
      getIdFromObject : function(object) {
        if (typeof(object) != 'string' && object.source == 'default')
        {
          if (object.id)
          {
            return object.id;
          }
          else if (object.roleId)
          {
            return object.roleId;
          }
        }
        else if (typeof(object) == 'string')
        {
          return object;
        }
        return null;
      },
      setSelectedRoleIds : function(roleIds, reload) {
        this.selectedRoleIds = [];

        if (roleIds != null)
        {
          if (!Ext.isArray(roleIds))
          {
            roleIds = [roleIds];
          }

          for (var i = 0; i < roleIds.length; i++)
          {
            var roleId = this.getIdFromObject(roleIds[i]);
            if (roleId != null)
            {
              this.selectedRoleIds.push(roleId);
            }
          }
        }

        if (this.selectedRoleIds.length == 0)
        {
          this.noRolesOnStart = true;
        }
        else
        {
          this.noRolesOnStart = false;
        }

        this.validate();

        if (reload)
        {
          this.reloadStore();
        }
      },
      setSelectedPrivilegeIds : function(privilegeIds, reload) {
        this.selectedPrivilegeIds = [];

        if (privilegeIds != null)
        {
          if (!Ext.isArray(privilegeIds))
          {
            privilegeIds = [privilegeIds];
          }

          for (var i = 0; i < privilegeIds.length; i++)
          {
            var privilegeId = this.getIdFromObject(privilegeIds[i]);
            if (privilegeId != null)
            {
              this.selectedPrivilegeIds.push(privilegeId);
            }
          }
        }

        this.validate();

        if (reload)
        {
          this.reloadStore();
        }
      },
      addSelectedRoleId : function(roleIds, reload) {
        if (roleIds)
        {
          if (!Ext.isArray(roleIds))
          {
            roleIds = [roleIds];
          }

          for (var i = 0; i < roleIds.length; i++)
          {
            var roleId = this.getIdFromObject(roleIds[i]);
            if (roleId != null)
            {
              this.selectedRoleIds.push(roleId);
            }
          }

          this.validate();

          if (reload)
          {
            this.reloadStore();
          }
        }
      },
      addSelectedPrivilegeId : function(privilegeIds, reload) {
        if (privilegeIds)
        {
          if (!Ext.isArray(privilegeIds))
          {
            privilegeIds = [privilegeIds];
          }

          for (var i = 0; i < privilegeIds.length; i++)
          {
            var privilegeId = this.getIdFromObject(privilegeIds[i]);
            if (privilegeId != null)
            {
              this.selectedPrivilegeIds.push(privilegeId);
            }
          }

          this.validate();

          if (reload)
          {
            this.reloadStore();
          }
        }
      },
      reloadStore : function() {
        this.storeProxy.conn.jsonData.data.selectedRoleIds = this.selectedRoleIds;
        this.storeProxy.conn.jsonData.data.selectedPrivilegeIds = this.selectedPrivilegeIds;
        this.storeProxy.conn.jsonData.data.userId = this.userId;
        this.store.load({
              params : {
                start : 0
              }
            });
      },
      getSelectedRoleIds : function() {
        return this.selectedRoleIds;
      },
      getSelectedPrivilegeIds : function() {
        return this.selectedPrivilegeIds;
      },
      getRoleNameFromId : function(id) {
        var rec = this.store.getById(id);

        if (rec)
        {
          return rec.get('name');
        }

        return id;
      },
      validate : function() {
        if (!this.userId && this.selectedRoleIds.length == 0 && this.selectedPrivilegeIds.length == 0)
        {
          var elp = this.getEl();

          if (!this.errorEl)
          {
            this.errorEl = elp.createChild({
                  cls : 'x-form-invalid-msg'
                });
            this.errorEl.setWidth(elp.getWidth(true));
            this.errorEl.setStyle('border: 0 solid #fff');
          }
          this.errorEl.update('You must select at least 1 role' + (this.usePrivileges ? ' or privilege' : ''));
          elp.setStyle({
                'background-color' : '#fee',
                border : '1px solid #dd7870'
              });
          Ext.form.Field.msgFx['normal'].show(this.errorEl, this);
          return false;
        }

        this.clearValidation();
        return true;
      },
      clearValidation : function() {
        if (this.errorEl)
        {
          this.getEl().setStyle({
                'background-color' : '#FFFFFF',
                border : '1px solid #B5B8C8'
              });
          Ext.form.Field.msgFx['normal'].hide(this.errorEl, this);
        }
      },
      setUserId : function(userId) {
        this.userId = userId;
        this.storeProxy.conn.jsonData.data.userId = this.userId;
      }
    });

Ext.reg('rolemanager', RoleManager);

/**
 * A RoleSelectorGrid is used to display the roles and privileges (optional) in a grid with checkboxes for simple selection
 * The grid supports pagination
 *  
 * showPrivileges - boolean flag, if true will show the privileges along with the roles 
 * selectedRoleIds - role ids that should show as selected 
 * selectedPrivilegeIds - privilege ids that should show as selected
 */
RoleSelectorGrid = function(config) {
  var config = config || {};

  var defaultConfig = {
    title : 'Manage Roles' + (this.usePrivileges ? ' and Privileges' : ''),
    trackMouseOver : false,
    loadMask : true,
    usePrivileges : true,
    hiddenRoleIds : [],
    hiddenPrivilegeIds : [],
    selectedRoleIds : [],
    selectedPrivilegeIds : []
  };
  //apply the config and defaults to 'this'
  Ext.apply(this, config, defaultConfig);

  //we have a predefined proxy here, so that we can post json data
  //and it is stored in this. so that we can easily change the json
  //params
  this.storeProxy = new Ext.data.HttpProxy({
        method : 'POST',
        url : Sonatype.config.servicePath + '/rolesAndPrivs',
        jsonData : {
          data : {
            name : null,
            noPrivileges : !this.usePrivileges,
            noRoles : false,
            selectedRoleIds : [],
            selectedPrivilegeIds : [],
            hiddenRoleIds : this.hiddenRoleIds,
            hiddenPrivilegeIds : this.hiddenPrivilegeIds
          }
        }
      });

  //our remote resource, that will be supplying the paginated content
  this.store = new Ext.data.JsonStore({
        root : 'data',
        id : 'id',
        totalProperty : 'totalCount',
        remoteSort : true,
        proxy : this.storeProxy,
        fields : [{
              name : 'id'
            }, {
              name : 'type'
            }, {
              name : 'name',
              sortType : Ext.data.SortTypes.asUCString
            }, {
              name : 'description'
            }, {
              name : 'external'
            }],
        listeners : {
          load : {
            fn : function(store, records, options) {
              this.applySelection();
            },
            scope : this
          }
        }
      });

  this.store.setDefaultSort('name', 'asc');

  //add a checkbox selection model to the grid
  this.sm = new Ext.grid.CheckboxSelectionModel({
        listeners : {
          rowselect : {
            fn : function(sm, idx, rec) {
              if (rec.get('type') == 'role')
              {
                var found = false;
                for (var i = 0; i < this.selectedRoleIds.length; i++)
                {
                  if (this.selectedRoleIds[i] == rec.get('id'))
                  {
                    found = true;
                    break;
                  }
                }

                if (!found)
                {
                  this.selectedRoleIds.push(rec.get('id'));
                }
              }
              else
              {
                var found = false;
                for (var i = 0; i < this.selectedPrivilegeIds.length; i++)
                {
                  if (this.selectedPrivilegeIds[i] == rec.get('id'))
                  {
                    found = true;
                    break;
                  }
                }

                if (!found)
                {
                  this.selectedPrivilegeIds.push(rec.get('id'));
                }
              }
            },
            scope : this
          },
          rowdeselect : {
            fn : function(sm, idx, rec) {
              if (rec.get('type') == 'role')
              {
                if (rec.get('external') == true)
                {
                  sm.selectRecords([rec], true);
                }
                for (var i = 0; i < this.selectedRoleIds.length; i++)
                {
                  if (this.selectedRoleIds[i] == rec.get('id'))
                  {
                    this.selectedRoleIds.remove(this.selectedRoleIds[i]);
                    break;
                  }
                }
              }
              else
              {
                var found = false;
                for (var i = 0; i < this.selectedPrivilegeIds.length; i++)
                {
                  if (this.selectedPrivilegeIds[i] == rec.get('id'))
                  {
                    this.selectedPrivilegeIds.remove(this.selectedPrivilegeIds[i])
                    break;
                  }
                }
              }
            },
            scope : this
          }
        }
      });

  var columns = [this.sm];

  columns.push({
        id : 'name',
        width : 300,
        header : 'Name',
        dataIndex : 'name',
        sortable : true,
        renderer : function(value, metaData, record, rowIndex, colIndex, store) {
          switch (record.get('type'))
          {
            case 'role' :
              metaData.css = 'roleRow';
              break;
            case 'privilege' :
              metaData.css = 'privilegeRow';
              break;
          }
          return String.format('<img class="placeholder" src="{0}"/>', Ext.BLANK_IMAGE_URL) + (record.get('external') ? ('<b>' + value + '</b>') : value);
        }
      }, {
        id : 'description',
        width : 400,
        header : 'Description',
        dataIndex : 'description',
        sortable : true
      });
      
  this.autoExpandColumn = 'description';

  //columns in the grid
  this.cm = new Ext.grid.ColumnModel({
        columns : columns
      });

  this.textFilter = new Ext.form.TextField({
        emptyText : 'Enter filter text...'
      });

  this.selectedFilter = new Ext.Button({
        text : 'Selected Only',
        enableToggle : true,
        tooltip : 'Add filter that will only show items selected',
        pressed : false
      });

  this.rolesFilter = new Ext.Button({
        text : 'Roles',
        enableToggle : true,
        tooltip : 'Add filter that will show roles',
        pressed : true
      });

  this.privilegesFilter = new Ext.Button({
        text : 'Privileges',
        enableToggle : true,
        tooltip : 'Add filter that will show privileges',
        pressed : true
      });

  // toolbar at top
  this.tbar = ['Filter: ', ' ', this.textFilter, '-', this.selectedFilter, '-'];

  if (this.usePrivileges)
  {
    this.tbar.push(this.rolesFilter, '-');
    this.tbar.push(this.privilegesFilter, '-');
  }

  this.tbar.push('->', '-', {
        text : 'Apply Filter',
        tooltip : 'Apply the filter parameter(s)',
        handler : this.applyFilter,
        scope : this
      }, '-', {
        text : 'Reset Filter',
        tooltip : 'Reset the filter to default selections',
        handler : this.resetFilter,
        scope : this
      }, '-');

  // paging bar on the bottom
  this.bbar = new Ext.PagingToolbar({
        pageSize : 25,
        store : this.store,
        displayInfo : true,
        displayMsg : 'Displaying roles' + (this.usePrivileges ? ' and privileges' : '') + ' {0} - {1} of {2}',
        emptyMsg : 'No roles' + (this.usePrivileges ? ' or privileges' : '') + ' to display'
      });

  //constructor call, adding the panel setup here
  RoleSelectorGrid.superclass.constructor.call(this, {});
};

Ext.extend(RoleSelectorGrid, Ext.grid.GridPanel, {
      //implement local onRender to load the first page of store
      onRender : function() {
        RoleSelectorGrid.superclass.onRender.apply(this, arguments);
        this.store.load({
              params : {
                start : 0,
                limit : 25
              }
            });
      },
      applySelection : function() {
        //suspend the events here, we dont want to update our selected items in mem, only in the grid
        this.getSelectionModel().suspendEvents();
        this.getSelectionModel().clearSelections();
        this.getSelectionModel().resumeEvents();
        var records = this.store.getRange();

        for (var i = 0; i < records.length; i++)
        {
          if (records[i].get('type') == 'role' && this.selectedRoleIds.indexOf(records[i].get('id')) != -1)
          {
            this.getSelectionModel().selectRecords([records[i]], true);
          }
          else if (records[i].get('type') == 'privilege' && this.selectedPrivilegeIds.indexOf(records[i].get('id')) != -1)
          {
            this.getSelectionModel().selectRecords([records[i]], true);
          }
        }
      },
      setSelectedRoleIds : function(roleIds, reload) {
        this.selectedRoleIds = [];

        if (roleIds != null)
        {
          if (!Ext.isArray(roleIds))
          {
            roleIds = [roleIds];
          }

          for (var i = 0; i < roleIds.length; i++)
          {
            if (typeof(roleIds[i]) != 'string')
            {
              if (roleIds[i].id)
              {
                this.selectedRoleIds.push(roleIds[i].id);
              }
              else if (roleIds[i].roleId)
              {
                this.selectedRoleIds.push(roleIds[i].roleId);
              }
            }
            else
            {
              this.selectedRoleIds.push(roleIds[i]);
            }
          }
        }

        if (reload)
        {
          this.applyFilter();
        }
        else
        {
          this.applySelection();
        }
      },
      setSelectedPrivilegeIds : function(privilegeIds, reload) {
        this.selectedPrivilegeIds = [];

        if (privilegeIds != null)
        {
          if (!Ext.isArray(privilegeIds))
          {
            privilegeIds = [privilegeIds];
          }

          for (var i = 0; i < privilegeIds.length; i++)
          {
            if (typeof(privilegeIds[i]) != 'string')
            {
              this.selectedPrivilegeIds.push(privilegeIds[i].id);
            }
            else
            {
              this.selectedPrivilegeIds.push(privilegeIds[i]);
            }
          }
        }

        if (reload)
        {
          this.applyFilter();
        }
        else
        {
          this.applySelection();
        }
      },
      getSelectedRoleIds : function() {
        return this.selectedRoleIds;
      },
      getSelectedPrivilegeIds : function() {
        return this.selectedPrivilegeIds;
      },
      applyFilter : function() {
        this.storeProxy.conn.jsonData.data.name = this.textFilter.getValue();

        if (this.selectedFilter.pressed)
        {
          this.storeProxy.conn.jsonData.data.selectedRoleIds = this.selectedRoleIds;
          this.storeProxy.conn.jsonData.data.selectedPrivilegeIds = this.selectedPrivilegeIds;
        }
        else
        {
          this.storeProxy.conn.jsonData.data.selectedRoleIds = [];
          this.storeProxy.conn.jsonData.data.selectedPrivilegeIds = [];
        }

        if (!this.rolesFilter.pressed)
        {
          this.storeProxy.conn.jsonData.data.noRoles = true;
        }
        else
        {
          this.storeProxy.conn.jsonData.data.noRoles = false;
        }

        if (!this.privilegesFilter.pressed || !this.showPrivileges)
        {
          this.storeProxy.conn.jsonData.data.noPrivileges = true;
        }
        else
        {
          this.storeProxy.conn.jsonData.data.noPrivileges = false;
        }

        this.store.load({
              params : {
                start : 0,
                limit : 25
              }
            });
      },
      resetFilter : function() {
        this.textFilter.setValue(null);
        this.selectedFilter.toggle(false);
        this.rolesFilter.toggle(true);
        this.privilegesFilter.toggle(true);

        this.storeProxy.conn.jsonData.data.name = null;
        this.storeProxy.conn.jsonData.data.selectedRoleIds = [];
        this.storeProxy.conn.jsonData.data.selectedPrivilegeIds = [];
        this.storeProxy.conn.jsonData.data.noRoles = false;
        this.storeProxy.conn.jsonData.data.noPrivileges = !this.showPrivileges;

        this.store.load({
              params : {
                start : 0,
                limit : 25
              }
            });
      }
    });

Ext.reg('roleselector', RoleSelectorGrid);
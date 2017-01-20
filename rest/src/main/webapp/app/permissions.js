// Permissions
tsApp.run([ 'securityService', function(securityService) {
  console.debug('Adding application permissions for UI visibility');

  // permissions for determining visibility in ui
  securityService.addPermission('EditProjectOrUser', {
    'REVIEWER' : false,
    'AUTHOR' : false,
    'ADMINISTRATOR' : true,
    'APP_USER' : true,
    'APP_ADMINISTRATOR' : true
  });
  securityService.addPermission('AddProjectOrUser', {
    'REVIEWER' : false,
    'AUTHOR' : false,
    'EDITOR5' : false,
    'ADMINISTRATOR' : false,
    'APP_USER' : true,
    'APP_ADMINISTRATOR' : true
  });

} ]);
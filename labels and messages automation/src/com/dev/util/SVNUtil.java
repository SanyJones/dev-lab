package com.dev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.dev.main.LabelMessageValidator;

/**
 *
 * @author Sany Jones R
 * @since 1.0
 */
public class SVNUtil
{
	public SVNUtil(){
		
	}
	public static void doRevert(){
		try {
			File[] checkOutLocation = new File[3];
			checkOutLocation[0] = new File(LabelMessageValidator.scriptCheckoutLocation);
			checkOutLocation[1] = new File(LabelMessageValidator.labelsCheckoutLocation);
			checkOutLocation[2] = new File(LabelMessageValidator.messagesCheckoutLocation);
			//SVNURL url = SVNURL.parseURIEncoded(LabelMessageValidator.propertyValues.get(PropertyConstants.SCRIPT_URL));
			SVNClientManager svnClientManager = SVNClientManager.newInstance();
			SVNWCClient svnWCClient = svnClientManager.getWCClient();
			//svnWCClient.doCleanup(scriptLocation);
			svnWCClient.doRevert(checkOutLocation, SVNDepth.INFINITY, null);
			LabelMessageValidator.proceed = true;
			/*SVNUpdateClient svnpdateClient = svnClientManager.getUpdateClient();
			svnpdateClient.doUpdate(scriptLocation, SVNRevision.HEAD, SVNDepth.INFINITY, true, false);*/
		} catch (SVNException e) {
			System.out.println("Error occurred in svn revert. Do manual updation and enter y/n to proceed:");
			try {
				if(new BufferedReader(new InputStreamReader(System.in)).readLine().contains("y")){
					LabelMessageValidator.proceed = true;
				}
			} catch (IOException e1) {
				
			}
		}
	}
	
	
	public static void main(String args[]){
		doRevert();
	}
}

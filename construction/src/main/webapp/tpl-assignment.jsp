<!-- Template for assignment -->
<script type="text/x-kendo-tmpl" id="tpl-assignment">
	<li>
# if (data.status == 'Active') { #
	<div class="assignment-tile sw-assignment-active">
		<div class="sw-assignment-active-indicator sw-assignment-status-indicator" style="height: 5px;"></div>
# } else if (data.status == 'Missing') { #
	<div class="assignment-tile sw-assignment-missing">
		<div class="sw-assignment-missing-indicator sw-assignment-status-indicator" style="height: 5px;"></div>
# } else { #
	<div class="assignment-tile sw-assignment-released">
		<div class="sw-assignment-status-indicator" style="height: 5px;"></div>
# } #
			<img class="asset-image" style="background-image: url(#:associatedHardware.imageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="asset-banner">Tracked Asset</span>
			<img class="device-image" style="background-image: url(#:device.assetImageUrl#); background-size: contain; background-repeat: no-repeat; background-position: 50% 50%;">
			<span class="device-banner">Tracking Device</span>
			<div class="assignment-data">
				<h1>#:associatedHardware.name#</h1>
				<div class="asset-details">
					<h2><span class="fldlabel">VIN:</span> #:metadata[0].value#</h2>
					<h2><span class="fldlabel">SKU:</span> #:associatedHardware.sku#</h2>
				</div>
				<div style="height: 10px;"></div>
				<h1>#:device.assetName#</h1>
				<div class="asset-details">
					<h2><span class="fldlabel">HWID:</span> #:device.hardwareId#</h2>
					<h2><span class="fldlabel">Info:</span> #:device.comments#</h2>
				</div>
			</div>
			<div class="assignment-state">
				<div class="mx-view">
					<div class="mx-value">#:state['fuel.level'].value#</div>
					<div class="mx-title">Fuel Level</div>
					<div class="mx-icon"><i class="icon-road"></i></div>
				</div>
				<div style="height: 10px;"></div>
				<div class="mx-view">
					<div class="mx-value">#:state['engine.temperature'].value#</div>
					<div class="mx-title">Engine Temperature</div>
					<div class="mx-icon"><i class="icon-fire"></i></div>
				</div>
			</div>
		</div>
	</div>
	</li>
</script>
